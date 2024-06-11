package sc.springProject.controllers;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ExtendWith(EmbeddedPostgresConfiguration.EmbeddedPostgresExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EmbeddedPostgresConfiguration.class})
@NoArgsConstructor
public class UserControllerTest {

    @Autowired
    @Lazy
    private UserController userController;

    @Autowired
    @Lazy
    private UserRepo userRepo;

    @Autowired
    @Lazy
    private DepartmentRepo departmentRepo;

    @org.junit.Test
    public void test() throws Exception{
        assertThat(userController).isNotNull();
    }

    @Test
    public void dbtest() throws Exception{
        Optional<Department> department = departmentRepo.findById(1L);
        assertThat(department).isNotNull();
        userRepo.save(new User("Vlad", 15, "+37544929229", department.get()));
    }
}
