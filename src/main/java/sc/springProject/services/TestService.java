package sc.springProject.services;

import jakarta.persistence.EntityManager;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;
import sc.springProject.repositories.DepartmentRepository;
import sc.springProject.repositories.UserRepository;

import java.util.Arrays;
import static java.lang.Math.random;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {

    @NonNull
    private UserRepository userRepository;

    @NonNull
    private UserService userService;

    @NonNull
    private DepartmentRepository departmentRepository;

    @NonNull
    private EntityManager entityManager;

    @Setter
    private int increaseSalaryNumber;

    @Setter
    private long changingUserId;

    @Setter
    private int createdUserSalary;

    @Transactional
    public void increaseSalaryTest(){
        int taskNum = (int)(random() * 100);
        log.info("Task {} started", taskNum);;
        User user = userRepository.findWithLockingById(changingUserId).get();
        user.setSalary(user.getSalary() + increaseSalaryNumber);
        userRepository.save(user);
        log.info("Task {} finished", taskNum);
    }

    public void addUser(){
        String username = "User" + (int)(random() * 1000);
        log.info("Creating {}...", username);

        User user = User.builder()
                .name(username)
                .age(20)
                .salary(createdUserSalary)
                .department(departmentRepository.findById(2L).get())
                .build();
        userRepository.saveAndFlush(user);
        entityManager.clear();

        userService.updateDepartmentAverageSalary(2L);
        log.info("{} created!!!", username);
    }
}
