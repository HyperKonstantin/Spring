package sc.springProject.database;

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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ContextConfiguration;
import sc.springProject.configuration.EmbeddedPostgresConfiguration;
import sc.springProject.entities.User;
import sc.springProject.repositories.DepartmentRepo;
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
@EnableScheduling
@EnableAsync
public class DatabaseThreadPoolTest {

    @Autowired
    private DepartmentRepo departmentRepo;

    @Autowired
    private UserRepo userRepo;

    @Test
    @Order(1)
    public void addUsersParrllelTest() throws ExecutionException, InterruptedException {
        int CreatedUserCount = 500;

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future> futures = new ArrayList<>();

        for (int i = 0; i < CreatedUserCount; i++){
            futures.add(executorService.submit(this::addUser));
        }

        for (Future i : futures) {
            i.get();
        }

        executorService.shutdown();
        Assert.assertEquals(CreatedUserCount, userRepo.findByNameIsStartingWith("User").size());
    }

    public void addUser(){
        String username = "User" + (int)(random() * 1000);
        log.info("Creating {}...", username);
        User user = new User(username, 20, 200, departmentRepo.findById(1L).get());
        userRepo.save(user);
        log.info("{} created!!!", username);
    }


}
