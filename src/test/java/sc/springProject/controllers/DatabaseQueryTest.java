package sc.springProject.controllers;


import org.junit.Assert;
import org.junit.Test;
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

@RunWith(SpringRunner.class)
@DataJpaTest
@ExtendWith(EmbeddedPostgresConfiguration.EmbeddedPostgresExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EmbeddedPostgresConfiguration.class})
public class DatabaseQueryTest {

    @Autowired
    @Lazy
    private UserRepo userRepo;

    @Autowired
    @Lazy
    private DepartmentRepo departmentRepo;

    @Test
    public void findUserByIdTest() throws Exception{
        Optional<User> user = userRepo.findById(1L);
        Assert.assertFalse(user.isEmpty());
        Assert.assertEquals("Kostya", user.get().getName());
    }

    @Test
    public void findUserByNameTest() throws Exception{
        List<User> users = userRepo.findByName("Kostya");

        Assert.assertEquals(1, users.size());
        Assert.assertEquals("Kostya", users.get(0).getName());
    }

    @Test
    public void ChangeUserDepartmentTest() throws Exception{
        Department department = departmentRepo.findFirstById(2);
        userRepo.changeDepartmentId(1, department);
        User user = userRepo.findById(1L).get();

        Assert.assertEquals(2, user.getDepartment().getId());
    }

    @Test
    public void ChangeUserNameTest() throws Exception{
        userRepo.changeName(1, "Konstantin");
        Optional<User> user = userRepo.findById(1L);

        Assert.assertFalse(user.isEmpty());
        Assert.assertEquals("Konstantin", user.get().getName());
    }
}
