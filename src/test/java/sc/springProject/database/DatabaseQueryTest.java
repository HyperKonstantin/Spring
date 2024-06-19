package sc.springProject.database;


import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import sc.springProject.configuration.EmbeddedPostgresConfiguration;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;
import sc.springProject.repositories.DepartmentRepository;
import sc.springProject.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@ExtendWith(EmbeddedPostgresConfiguration.EmbeddedPostgresExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EmbeddedPostgresConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class DatabaseQueryTest {

    @Autowired
    private EntityManager entityManager;  // need for clear entity cache

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private DepartmentRepository departmentRepo;

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

        Department department = departmentRepo.findById(2).get();
        User user = userRepo.findById(1).get();
        user.setDepartment(department);
        userRepo.save(user);

        Assert.assertEquals(2, userRepo.findById(1L).get().getDepartment().getId());
    }

    @Test
    @Order(1)
    public void ChangeUserNameTest() throws Exception{
        log.info("ChangeUserNameTest Started!");

        User user = userRepo.findById(1).get();
        user.setName("Konstantin");
        userRepo.save(user);

        Assert.assertEquals("Konstantin", userRepo.findById(1L).get().getName());
    }
}
