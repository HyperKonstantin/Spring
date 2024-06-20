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
import sc.springProject.configuration.EmbeddedPostgresConfiguration;
import sc.springProject.repositories.DepartmentRepository;
import sc.springProject.repositories.UserRepository;
import sc.springProject.services.TestService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@DataJpaTest
@ExtendWith(EmbeddedPostgresConfiguration.EmbeddedPostgresExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EmbeddedPostgresConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class DatabaseThreadPoolTest {

    final int THREAD_TASKS_COUNT = 20;
    final int COUNT_OF_THREADS = 4;
    final long CHANGING_USER_ID = 1;
    final int INCREASING_SALARY_NUMBER = 100;
    final int CREATED_USER_SALARY = 250;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestService testService;

    @Test
    @Order(1)
    public void synchronizedChangeUserSalaryTest() {
        int salaryBeforeIncreasing = userRepository.findById(CHANGING_USER_ID).get().getSalary();
        testService.setIncreaseSalaryNumber(INCREASING_SALARY_NUMBER);
        testService.setChangingUserId(CHANGING_USER_ID);

        ExecutorService executorService = Executors.newFixedThreadPool(COUNT_OF_THREADS);
        List<Future> futures = runThreads(executorService, testService::increaseSalaryTest);

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
    @Order(2)
    public void updateAverageDepartmentSalaryTest(){
        testService.setCreatedUserSalary(CREATED_USER_SALARY);

        ExecutorService executorService = Executors.newFixedThreadPool(COUNT_OF_THREADS);
        List<Future> futures = runThreads(executorService, testService::addUser);

        joinThreads(futures);
        executorService.shutdown();
        entityManager.clear();

        Assert.assertEquals(THREAD_TASKS_COUNT, departmentRepository.findById(2L).get().getUsers().size());
        Assert.assertEquals(CREATED_USER_SALARY, departmentRepository.findById(2L).get().getAverageSalary(), 1);
    }
}
