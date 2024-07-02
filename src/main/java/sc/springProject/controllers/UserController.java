package sc.springProject.controllers;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sc.springProject.dto.UserDto;
import sc.springProject.entities.User;
import sc.springProject.services.UserService;

@Tag(name = "UserQuery", description = "работает с данными пользователя")
@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @Operation(
            summary = "Возвращает присок пользователей"
    )
    @Timed("GetUsersRequest")
    @GetMapping("/get")
    public ResponseEntity<?> getUsers(){
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @Operation(
            summary = "Добавляет пользователя",
            description = "Принимает параметры <strong>name, age</strong> и <strong>salary</strong> в теле запроса в формате JSON и <strong>id</strong> в url"
    )
    @PostMapping("/add/{id}")
    public ResponseEntity<?> addUser(@RequestBody User user, @PathVariable("id") long departmentId){
        UserDto userDto = userService.newUser(user, departmentId);

        if (userDto == null){
            return new ResponseEntity<>("указанный отдел отсутствует", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Находит пользователя по имени",
            description = "Принимает параметр <strong>name</strong> и возвращает пользователей с таким именем"
    )
    @GetMapping("/find")
    public ResponseEntity<?> findUser(@RequestParam String name) {
        return new ResponseEntity<>(userService.findUserByName(name), HttpStatus.OK);
    }

    @Operation(
            summary = "Удаляет пользователя",
            description = "Принимает параметр <strong>id</strong> пользователя и удаляет пользователя с этим id"
    )
    @GetMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam long id){
        UserDto userDto = userService.deleteUser(id);

        if (userDto == null){
            return new ResponseEntity<>("Такого пользователя нет", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Изменяет имя пользователя",
            description = "изменяет имя пользователя с некоторым <strong>id</strong> на параметр <strong>name</strong>"
    )
    @Transactional
    @GetMapping("/change-name")
    public ResponseEntity<?> changeUserName(@RequestParam long id, @RequestParam String name){
        UserDto userDto = userService.changeName(id, name);

        if (userDto == null){
            return new ResponseEntity<>("Пользователя с таким id нет!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/send-id")
    public ResponseEntity<?> sendUserId(@RequestParam long id){
        return userService.sendIdToNatsListener(id);
    }
}
