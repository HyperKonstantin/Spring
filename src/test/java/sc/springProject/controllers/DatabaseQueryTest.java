package sc.springProject.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import sc.springProject.configuration.EmbeddedPostgresConfiguration;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;
import sc.springProject.repositories.DepartmentRepo;
import sc.springProject.repositories.UserRepo;

import java.util.List;
import java.util.Optional;

//@RunWith(SpringRunner.class)
@DataJpaTest
@ExtendWith(EmbeddedPostgresConfiguration.EmbeddedPostgresExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EmbeddedPostgresConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class DatabaseQueryTest {

    @Autowired
    @Lazy
    private UserRepo userRepo;

    @Autowired
    @Lazy
    private DepartmentRepo departmentRepo;

    @Order(3)
    @Test
    public void findUserByIdTest() throws Exception{
        log.info("findUserByIdTest Started!");
        Optional<User> user = userRepo.findById(1L);
        Assert.assertFalse(user.isEmpty());
        Assert.assertEquals("Kostya", user.get().getName());
    }

    @Test
    @Order(2)
    public void findUserByNameTest() throws Exception{
        log.info("findUserByNameTest Started!");
        List<User> users = userRepo.findByName("Kostya");

        Assert.assertEquals(1, users.size());
        Assert.assertEquals("Kostya", users.get(0).getName());
    }

    @Test
    @Order(4)
    public void ChangeUserDepartmentTest() throws Exception{
        log.info("ChangeUserDepartmentTest Started!");
        Department department = departmentRepo.findFirstById(2);
        userRepo.changeDepartment(1, department);
        User user = userRepo.findById(1L).get();

        Assert.assertEquals(2, user.getDepartment().getId());
    }

    @Test
    @Order(1)
    public void ChangeUserNameTest() throws Exception{
        log.info("ChangeUserNameTest Started!");
        log.info("JSON: {}", (new ObjectMapper()).writeValueAsString(userRepo.findAll()));
        userRepo.changeName(1, "Konstantin");
        log.info("JSON: {}", (new ObjectMapper()).writeValueAsString(userRepo.findAll()));
        Optional<User> user = userRepo.findById(1L);

        Assert.assertFalse(user.isEmpty());
        Assert.assertEquals("Konstantin", user.get().getName());


    }
}
