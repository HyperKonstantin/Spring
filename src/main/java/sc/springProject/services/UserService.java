package sc.springProject.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sc.springProject.dto.UserDto;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;
import sc.springProject.kafka.KafkaProducer;
import sc.springProject.repositories.DepartmentRepository;
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
    private final KafkaProducer kafkaProducer;
    private final DepartmentRepository departmentRepository;
    private final DtoMapper dtoMapper;

    public List<UserDto> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(dtoMapper::mapToUserDto).toList();
    }

    @Transactional
    public UserDto newUser(User user, long departmentId){
        Optional<Department> department = departmentRepository.findById(departmentId);

        if (department.isEmpty()){
            return null;
        }

        user.setDepartment(department.get());
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
    public ResponseEntity<?> sendIdToListener(long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()){
            return new ResponseEntity<>("Пользователя с таким id не существует!", HttpStatus.BAD_REQUEST);
        }

        UserDto userDto = dtoMapper.mapToUserDto(userOptional.get());
        kafkaProducer.sendMessage((new ObjectMapper()).writeValueAsString(userDto));
        log.info("Sending user: {}", userDto.getName());

        return new ResponseEntity<>("Пользователь "+ userDto.getName() + " отправлен!", HttpStatus.OK) ;
    }

    @SneakyThrows
    @Transactional("kafkaTransactionManager")
    public ResponseEntity<?> sendTransactionalIdToListener(long id) {
        kafkaProducer.sendTransactionalMessage("sending user");

        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()){
            throw new RuntimeException("user is not exists");
        }

        UserDto userDto = dtoMapper.mapToUserDto(userOptional.get());
        kafkaProducer.sendTransactionalMessage((new ObjectMapper()).writeValueAsString(userDto));
        log.info("Sending user: {}", userDto.getName());

        return new ResponseEntity<>("Пользователь "+ userDto.getName() + " отправлен!", HttpStatus.OK);
    }
}