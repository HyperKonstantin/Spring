package sc.springProject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.engine.spi.ManagedEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sc.springProject.dto.UserDto;
import sc.springProject.repositories.DepartmentRepo;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;
import sc.springProject.repositories.UserRepo;
import sc.springProject.services.DtoMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "UserQuery", description = "работает с данными пользователя")
@CrossOrigin
@RestController
@AllArgsConstructor
public class UserController {

    private UserRepo userRepo;

    private DepartmentRepo departmentRepo;

    private DtoMapper dtoMapper;

    private EntityManager entityManager;

    @Operation(
            summary = "Возвращает присок пользователей"
    )
    @GetMapping("/get-users")
    public ResponseEntity<?> getUsers(){
        List<User> users = userRepo.findAll();
        List<UserDto> usersDto = users.stream().map(dtoMapper::mapToUserDto).toList();
        return new ResponseEntity<>(usersDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Добавляет пользователя",
            description = "Принимает параметры <strong>name, age</strong> и <strong>salary</strong> и создаёт пользователя"
    )
    @GetMapping("/add-user")
    public ResponseEntity<?> addUser(@RequestParam String name, @RequestParam int age, @RequestParam int salary, @RequestParam long departmentId){
        Optional<Department> department = departmentRepo.findById(departmentId);

        if (department.isEmpty())
            return new ResponseEntity<>("указанный отдел отсутствует", HttpStatus.BAD_REQUEST);

        User user = new User(name, age, salary, department.get());
        userRepo.save(user);
        return new ResponseEntity<>(dtoMapper.mapToUserDto(user), HttpStatus.OK);
    }

    @Operation(
            summary = "Находит пользователя по имени",
            description = "Принимает параметр <strong>name</strong> и возвращает пользователей с таким именем"
    )
    @GetMapping("/find-user")
    public ResponseEntity<?> findUser(@RequestParam String name) {
        List<UserDto> usersDto = userRepo.findByName(name).stream().map(dtoMapper::mapToUserDto).toList();
        return new ResponseEntity<>(usersDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Удаляет пользователя",
            description = "Принимает параметр <strong>id</strong> пользователя и удаляет пользователя с этим id"
    )
    @GetMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestParam long id){
        Optional<User> user = userRepo.findById(id);

        if (user.isEmpty())
            return new ResponseEntity<>("Такого пользователя нет", HttpStatus.BAD_REQUEST);

        userRepo.deleteById(id);
        return new ResponseEntity<>(dtoMapper.mapToUserDto(user.get()), HttpStatus.OK);
    }

    @Operation(
            summary = "Изменяет имя пользователя",
            description = "изменяет имя пользователя с некоторым <strong>id</strong> на параметр <strong>name</strong>"
    )
    @Transactional
    @GetMapping("/change-user-name")
    public ResponseEntity<?> changeUserName(@RequestParam long id, @RequestParam String name){

        if (userRepo.findById(id).isEmpty()){
            return new ResponseEntity<>("Пользователя с таким id нет!", HttpStatus.OK);
        }

        userRepo.changeName(id, name);
        entityManager.clear();
        UserDto userDto = dtoMapper.mapToUserDto(userRepo.findById(id).get());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

}
