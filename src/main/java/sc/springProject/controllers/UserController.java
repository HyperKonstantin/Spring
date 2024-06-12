package sc.springProject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sc.springProject.repositories.DepartmentRepo;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;
import sc.springProject.repositories.UserRepo;

import java.util.Optional;

@Tag(name = "UserQuery", description = "работает с данными пользователя")
@CrossOrigin
@RestController
@AllArgsConstructor
public class UserController {

    private UserRepo userRepo;

    private DepartmentRepo departmentRepo;

    @Operation(
            summary = "Возвращает присок пользователей"
    )
    @GetMapping("/get-users")
    public ResponseEntity<?> getUsers(){
        Iterable<User> users = userRepo.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(
            summary = "Добавляет пользователя",
            description = "Принимает параметры <strong>name, age</strong> и <strong>phone</strong> и возвращает пользователей с таким именем"
    )
    @GetMapping("/add-user")
    public ResponseEntity<?> addUser(@RequestParam String name, @RequestParam int age, @RequestParam String phone, @RequestParam long departmentId){
        Optional<Department> department = departmentRepo.findById(departmentId);

        if (department.isEmpty())
            return new ResponseEntity<>("указанный отдел отсутствует", HttpStatus.BAD_REQUEST);

        User user = new User(name, age, phone, department.get());
        userRepo.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(
            summary = "Находит пользователя по имени",
            description = "Принимает параметр <strong>name</strong> и возвращает пользователей с таким именем"
    )
    @GetMapping("/find-user")
    public ResponseEntity<?> findUser(@RequestParam String name) {
        return new ResponseEntity<>(userRepo.findByName(name), HttpStatus.OK);

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
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(
            summary = "Изменяет имя пользователя",
            description = "изменяет имя пользователя с некоторым <strong>id</strong> на параметр <strong>name</strong>"
    )
    @Transactional
    @GetMapping("/change-user-name")
    public ResponseEntity<?> changeUserName(@RequestParam long id, @RequestParam String name){
        userRepo.changeName(id, name);
        return new ResponseEntity<>(userRepo.findById(id), HttpStatus.OK);
    }

}
