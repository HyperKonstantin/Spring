package test.SpringProject.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.SpringProject.Entities.Department;
import test.SpringProject.Entities.User;
import test.SpringProject.Repositories.DepartmentRepo;
import test.SpringProject.Repositories.UserRepo;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Tag(name = "UserData", description = "работает с данными пользователя")
@CrossOrigin
@RestController
public class UserController {

    @Autowired
    private UserRepo userRepo;
    @Autowired
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
        userRepo.deleteById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
