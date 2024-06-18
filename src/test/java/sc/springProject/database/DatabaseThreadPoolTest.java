package sc.springProject.database;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import sc.springProject.configuration.EmbeddedPostgresConfiguration;
import sc.springProject.entities.Product;
import sc.springProject.entities.User;
import sc.springProject.repositories.DepartmentRepository;
import sc.springProject.repositories.ProductRepository;
import sc.springProject.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Math.random;

@DataJpaTest
@ExtendWith(EmbeddedPostgresConfiguration.EmbeddedPostgresExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EmbeddedPostgresConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class DatabaseThreadPoolTest {

    final int THREAD_TASKS_COUNT = 20;
    final int COUNT_OF_THREADS = 5;
    final long CHANGING_USER_ID = 1;
    final int INCREASING_SALARY_NUMBER = 100;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Order(3)
    public void addUsersTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(COUNT_OF_THREADS);
        List<Future> futures = new ArrayList<>();

        for (int i = 0; i < THREAD_TASKS_COUNT; i++){
            futures.add(executorService.submit(this::addUser));
        }

        joinThreads(futures);
        executorService.shutdown();

        Assert.assertEquals(THREAD_TASKS_COUNT, userRepository.findByNameIsStartingWith("User").size());
    }

    public void addUser(){
        String username = "User" + (int)(random() * 1000);
        log.info("Creating {}...", username);
        User user = new User(username, 20, 200, departmentRepository.findById(1L).get());
        userRepository.save(user);
        log.info("{} created!!!", username);
    }

    @Test
    @Order(2)
    public void synchronizedChangeUserSalaryTest() {
        int salaryBeforeIncreasing = userRepository.findById(CHANGING_USER_ID).get().getSalary();
        ExecutorService executorService = Executors.newFixedThreadPool(COUNT_OF_THREADS);
        List<Future> futures = runThreads(executorService, this::increaseSalary);

        joinThreads(futures);
        executorService.shutdown();
        entityManager.clear();

        int expectedSalary = salaryBeforeIncreasing + THREAD_TASKS_COUNT * INCREASING_SALARY_NUMBER;
        Assert.assertEquals(expectedSalary, userRepository.findById(CHANGING_USER_ID).get().getSalary());
    }


    public List<Future> runThreads(ExecutorService executorService, Runnable runnable) {
        List<Future> futures = new ArrayList<>();

        for (int i = 0; i < THREAD_TASKS_COUNT; i++){
            futures.add(executorService.submit(runnable));
        }

        return futures;
    }

    @Transactional
    public void increaseSalary(){
        int taskNum = (int)(random() * 100);
        log.info("Task {} started", taskNum);;
        User user = userRepository.findFirstById(CHANGING_USER_ID).get();
        user.setSalary(user.getSalary() + INCREASING_SALARY_NUMBER);
        userRepository.save(user);
        log.info("Task {} finished", taskNum);
    }

    public void joinThreads(List<Future> futures) {
        for (Future future : futures){
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    @Order(1)
    public void productTest(){
        int priceBeforeIncreasing = productRepository.findByName("apple").get().getPrice();
        ExecutorService executorService = Executors.newFixedThreadPool(COUNT_OF_THREADS);
        List<Future> futures = runThreads(executorService, this::increaseProductPrice);

        joinThreads(futures);
        executorService.shutdown();
        entityManager.clear();

        int expectedSalary = priceBeforeIncreasing + THREAD_TASKS_COUNT * 10;
        Assert.assertEquals(expectedSalary, productRepository.findByName("apple").get().getPrice());
    }

    @Transactional
    public void increaseProductPrice(){
        int taskNum = (int)(random() * 100);
        log.info("Task {} started", taskNum);

        Product product = productRepository.findById(1L).get();
        product.setPrice(product.getPrice() + 10);
        productRepository.save(product);

        log.info("Task {} finished", taskNum);
    }
}
