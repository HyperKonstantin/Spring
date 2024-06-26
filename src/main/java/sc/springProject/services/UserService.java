package sc.springProject.services;

import io.nats.client.Message;
import io.nats.client.impl.NatsMessage;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sc.springProject.dto.UserDto;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;
import sc.springProject.repositories.DepartmentRepository;
import sc.springProject.repositories.NatsRepository;
import sc.springProject.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Math.random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final String NATS_SUBJECT = "info.user";

    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final NatsRepository natsRepository;
    private final DepartmentRepository departmentRepository;
    private final DtoMapper dtoMapper;

    public List<UserDto> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(dtoMapper::mapToUserDto).toList();
    }

    @Transactional
    public UserDto newUser(String name, int age, int salary, long departmentId){
        Optional<Department> department = departmentRepository.findById(departmentId);

        if (department.isEmpty()){
            return null;
        }

        User user = new User(name, age, salary, department.get());
        userRepository.saveAndFlush(user);
        entityManager.clear();
        updateDepartmentAverageSalary(departmentId);

        return dtoMapper.mapToUserDto(user);
    }

    public void updateDepartmentAverageSalary(long departmentId){
        Department department = departmentRepository.findWithLockingById(departmentId).get();

        int[] usersSalaries = department.getUsers().stream().mapToInt(User::getSalary).toArray();
        department.setAverageSalary(Arrays.stream(usersSalaries).average().getAsDouble());

        departmentRepository.save(department);
    }

    public List<UserDto> findUserByName(String name){
        List<UserDto> usersDto = userRepository.findByName(name).stream().map(dtoMapper::mapToUserDto).toList();
        return usersDto;
    }

    public UserDto deleteUser(long id){
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        user.dismissDepartment();
        userRepository.delete(user);


        return dtoMapper.mapToUserDto(userOptional.get());
    }

    public UserDto changeName(long userId, String newName){
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()){
            return null;
        }

        User user = userOptional.get();
        user.setName(newName);
        userRepository.save(user);

        return dtoMapper.mapToUserDto(user);
    }

    @SneakyThrows
    @Transactional
    public void stressTest(){
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Future> futures = new ArrayList<>();


        for (int i = 0; i < 30; i++){
            futures.add(executorService.submit(this::addUser));
        }

        for (Future future : futures){
            future.get();
        }
        executorService.shutdown();
    }

    public void addUser(){
        String username = "User" + (int)(random() * 1000);
        log.info("Creating {}...", username);

        User user = User.builder()
                .name(username)
                .age(20)
                .salary(250)
                .department(departmentRepository.findById(52L).get())
                .build();
        userRepository.saveAndFlush(user);
        entityManager.clear();

        try {
            updateDepartmentAverageSalary(52L);
        }
        catch (ObjectOptimisticLockingFailureException e){
            log.info("Locked ({})", username);
        }
        log.info("{} created!!!", username);
    }

    public ResponseEntity<?> sendIdToNatsListener(long id) {
        natsRepository.send(String.valueOf(id), "info.user");
        log.info("Sending message: {}", id);

        Message response = natsRepository.getResponce();
        return new ResponseEntity<>(new String(response.getData()), HttpStatus.OK) ;
    }
}