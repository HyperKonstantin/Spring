package sc.springProject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sc.springProject.repositories.DepartmentRepo;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;
import sc.springProject.repositories.UserRepo;

import java.util.List;
import java.util.Optional;

@Slf4j
@Tag(name = "DepartmentQuery", description = "запросы с отделами")
@RestController
@CrossOrigin
@AllArgsConstructor
public class DepartmentController {

    private UserRepo userRepo;

    private DepartmentRepo departmentRepo;

    @Operation(summary = "Возвращает все отделы")
    @GetMapping("/get-departments")
    public ResponseEntity<?> getDepartments(){
        log.info("Get query");
        return new ResponseEntity<>(departmentRepo.findAll(), HttpStatus.OK);
    }

    @Operation(summary = "Находит всех сотрудников отдела по его id")
    @GetMapping("/find-by-department-id")
    public ResponseEntity<?> FindUserByDepartmentId(@RequestParam long departmentId){
        Optional<Department> department = departmentRepo.findById(departmentId);

        if (department.isEmpty()) {
            return new ResponseEntity<>("такого отдела нет", HttpStatus.BAD_REQUEST);
        }

        List<User> users = userRepo.findByDepartment(department.get());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Добавляет отдел")
    @GetMapping("/add-department")
    public ResponseEntity<?> addDepartment(@RequestParam String name){
        Department department = new Department(name);
        departmentRepo.save(department);
        return new ResponseEntity<>(department, HttpStatus.OK);
    }
}