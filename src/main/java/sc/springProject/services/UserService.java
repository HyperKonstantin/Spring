package sc.springProject.services;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;
import sc.springProject.entities.UserView;
import sc.springProject.kafka.KafkaMessageProducer;
import sc.springProject.kafka.KafkaObjectProducer;
import sc.springProject.repositories.DepartmentRepository;
import sc.springProject.repositories.UserRepository;
import sc.springProject.repositories.UserViewRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final String NATS_SUBJECT = "info.user";

    private final UserViewRepository userViewRepository;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final KafkaObjectProducer kafkaObjectProducer;
    private final KafkaMessageProducer kafkaMessageProducer;
    private final DepartmentRepository departmentRepository;

    public List<UserView> getAllUsers(){
        return userViewRepository.findAll();
    }

    @Transactional
    public UserView newUser(User user, long departmentId){
        Optional<Department> department = departmentRepository.findById(departmentId);

        if (department.isEmpty()){
            return null;
        }

        user.setDepartment(department.get());
        userRepository.saveAndFlush(user);
        entityManager.clear();
        updateDepartmentAverageSalary(departmentId);

        return new UserView(user);
    }

    public void updateDepartmentAverageSalary(long departmentId){
        Department department = departmentRepository.findWithLockingById(departmentId).get();

        int[] usersSalaries = department.getUsers().stream().mapToInt(User::getSalary).toArray();
        department.setAverageSalary(Arrays.stream(usersSalaries).average().getAsDouble());

        departmentRepository.save(department);
    }

    public List<UserView> findUserByName(String name){
        return userViewRepository.findByName(name);
    }

    public UserView deleteUser(long id){
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        user.dismissDepartment();
        userRepository.delete(user);


        return new UserView(user);
    }

    public UserView changeName(long userId, String newName){
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()){
            return null;
        }

        User user = userOptional.get();
        user.setName(newName);
        userRepository.save(user);

        return new UserView(user);
    }

    @SneakyThrows
    public ResponseEntity<?> sendIdToListener(long id) {
        Optional<UserView> userOptional = userViewRepository.findById(id);

        if (userOptional.isEmpty()){
            return new ResponseEntity<>("Пользователя с таким id не существует!", HttpStatus.BAD_REQUEST);
        }

        UserView userView = userOptional.get();
        kafkaObjectProducer.sendMessage(userView);
        log.info("Sending user: {}", userView.getName());

        return new ResponseEntity<>("Пользователь "+ userView.getName() + " отправлен!", HttpStatus.OK) ;
    }

    @SneakyThrows
    @Transactional("kafkaTransactionManager")
    public ResponseEntity<?> sendTransactionalIdToListener(long id) {

        kafkaMessageProducer.sendMessage("message","sending user");

        Optional<UserView> userOptional = userViewRepository.findById(id);

        if (userOptional.isEmpty()){
            throw new RuntimeException("user is not exists");
        }

        UserView userView = userOptional.get();
        kafkaObjectProducer.sendTransactionalMessage(userView);
        log.info("Sending user: {}", userView.getName());

        return new ResponseEntity<>("Пользователь "+ userView.getName() + " отправлен!", HttpStatus.OK);
    }

    @SneakyThrows
//    @Transactional("kafkaTransactionManager")
    public ResponseEntity<?> sendAllUsers() {
        List<UserView> users = userViewRepository.findAll();

        for (UserView user : users){
            kafkaObjectProducer.sendUsersToBatchConsume(user);
        }
        return new ResponseEntity<>("Пользователи отправлены", HttpStatus.OK);
    }

    @SneakyThrows
    public ResponseEntity<?> kafkaTest(int count, int time) {
        for (int i = 0; i < count; i++){
            kafkaObjectProducer.sendUsersToBatchConsume(userViewRepository.findAll().get(0));
            Thread.sleep(time);
        }
        return new ResponseEntity<>("finish!", HttpStatus.OK);
    }
}