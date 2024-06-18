package sc.springProject.services;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sc.springProject.dto.UserDto;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;
import sc.springProject.repositories.DepartmentRepository;
import sc.springProject.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private DepartmentRepository departmentRepository;

    private DtoMapper dtoMapper;

    public List<UserDto> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(dtoMapper::mapToUserDto).toList();
    }

    public UserDto newUser(String name, int age, int salary, long departmentId){
        Optional<Department> department = departmentRepository.findById(departmentId);

        if (department.isEmpty()){
            return null;
        }

        User user = new User(name, age, salary, department.get());
        userRepository.save(user);

        return dtoMapper.mapToUserDto(user);
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

        userRepository.deleteById(id);

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






    public UserDto LockingUpdateUser(){
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 20; i++){
            executorService.execute(this::increaseSalary);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();

        return dtoMapper.mapToUserDto(userRepository.findById(1L).get());
    }

    @Transactional("transactionManagerName")
    public void increaseSalary(){
        User user = userRepository.findById(1L).get();
        user.setSalary(user.getSalary() + 100);
        userRepository.save(user);
    }

}
