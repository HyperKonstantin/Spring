package sc.springProject.services;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sc.springProject.entities.User;
import sc.springProject.repositories.DepartmentRepository;
import sc.springProject.repositories.UserRepository;

import static java.lang.Math.random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserTestService {

    @NonNull
    private UserRepository userRepository;

    @NonNull
    private DepartmentRepository departmentRepository;

    @Setter
    private int increaseSalaryNumber;

    @Setter
    private long changingUserId;

    @Transactional
    public void increaseSalaryTest(){
        int taskNum = (int)(random() * 100);
        log.info("Task {} started", taskNum);;
        User user = userRepository.findFirstById(changingUserId).get();
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
                .salary(200)
                .department(departmentRepository.findById(1L).get()).build();
        userRepository.save(user);
        log.info("{} created!!!", username);
    }
}
