package sc.springProject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sc.springProject.entities.Department;
import sc.springProject.entities.UserView;
import sc.springProject.services.DepartmentService;

import java.util.List;

@Slf4j
@Tag(name = "DepartmentQuery", description = "запросы с отделами")
@RestController
@CrossOrigin
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Operation(summary = "Возвращает все отделы")
    @GetMapping("/get")
    public ResponseEntity<?> getDepartments(){
        return new ResponseEntity<>(departmentService.getAllDepartments(), HttpStatus.OK);
    }

    @Operation(summary = "Находит всех сотрудников отдела по его id")
    @GetMapping("/find-users")
    public ResponseEntity<?> FindUserByDepartmentId(@RequestParam("id") long departmentId){
        List<UserView> users = departmentService.getAllUsersFromDepartment(departmentId);

        if (users == null) {
            return new ResponseEntity<>("такого отдела нет", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Добавляет отдел")
    @PostMapping("/add")
    public ResponseEntity<?> addDepartment(@RequestBody Department department){
        departmentService.addNewDepartment(department);
        return new ResponseEntity<>(department, HttpStatus.CREATED);
    }

    @Transactional
    @GetMapping("/swap-user-departments")
    public ResponseEntity<?> swapDepartments(@RequestParam long firstUserId, @RequestParam long secondUserId){
        try {
            departmentService.swapUserDepartments(firstUserId, secondUserId);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Отделы заменены!", HttpStatus.OK);
    }
}
