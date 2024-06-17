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
import sc.springProject.repositories.DepartmentRepo;
import sc.springProject.repositories.ProductRepo;
import sc.springProject.repositories.UserRepo;

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
    final int COUNT_OF_THREADS = 1;
    final long CHANGING_USER_ID = 1;
    final int INCREASING_SALARY_NUMBER = 100;

    @Autowired
    private DepartmentRepo departmentRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

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

        Assert.assertEquals(THREAD_TASKS_COUNT, userRepo.findByNameIsStartingWith("User").size());
    }

    public void addUser(){
        String username = "User" + (int)(random() * 1000);
        log.info("Creating {}...", username);
        User user = new User(username, 20, 200, departmentRepo.findById(1L).get());
        userRepo.save(user);
        log.info("{} created!!!", username);
    }

    @Test
    @Order(2)
    public void synchronizedChangeUserSalaryTest() {
        int salaryBeforeIncreasing = userRepo.findById(CHANGING_USER_ID).get().getSalary();
        ExecutorService executorService = Executors.newFixedThreadPool(COUNT_OF_THREADS);
        List<Future> futures = runThreads(executorService, this::increaseSalary);

        joinThreads(futures);
        executorService.shutdown();
        entityManager.clear();

        int expectedSalary = salaryBeforeIncreasing + THREAD_TASKS_COUNT * INCREASING_SALARY_NUMBER;
        Assert.assertEquals(expectedSalary, userRepo.findById(CHANGING_USER_ID).get().getSalary());
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
        User user = userRepo.findById(CHANGING_USER_ID).get();
        user.setSalary(user.getSalary() + INCREASING_SALARY_NUMBER);
        userRepo.save(user);
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
    @Transactional
    @Order(1)
    public void productTest(){
        int priceBeforeIncreasing = productRepo.findFirstByName("apple").get().getPrice();
        ExecutorService executorService = Executors.newFixedThreadPool(COUNT_OF_THREADS);
        List<Future> futures = runThreads(executorService, this::increaseProductPrice);

        joinThreads(futures);
        executorService.shutdown();
        entityManager.clear();

        int expectedSalary = priceBeforeIncreasing + THREAD_TASKS_COUNT * 10;
        Assert.assertEquals(expectedSalary, productRepo.findFirstByName("apple").get().getPrice());
    }

    @Transactional
    public void increaseProductPrice(){
        int taskNum = (int)(random() * 100);
        log.info("Task {} started", taskNum);

        Product product = productRepo.findById(1L).get();
        product.setPrice(product.getPrice() + 10);
        productRepo.save(product);

        log.info("Task {} finished", taskNum);
    }
}
