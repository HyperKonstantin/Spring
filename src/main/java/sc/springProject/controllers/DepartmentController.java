package sc.springProject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sc.springProject.dto.UserDto;

import sc.springProject.services.DepartmentService;

import java.util.List;

@Slf4j
@Tag(name = "DepartmentQuery", description = "запросы с отделами")
@RestController
@CrossOrigin
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Operation(summary = "Возвращает все отделы")
    @GetMapping("/get-departments")
    public ResponseEntity<?> getDepartments(){
        return new ResponseEntity<>(departmentService.getAllDepartments(), HttpStatus.OK);
    }

    @Operation(summary = "Находит всех сотрудников отдела по его id")
    @GetMapping("/find-by-department-id")
    public ResponseEntity<?> FindUserByDepartmentId(@RequestParam long departmentId){
        List<UserDto> usersDto = departmentService.getAllUsersFromDepartment(departmentId);

        if (usersDto == null) {
            return new ResponseEntity<>("такого отдела нет", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(usersDto, HttpStatus.OK);
    }

    @Operation(summary = "Добавляет отдел")
    @GetMapping("/add-department")
    public ResponseEntity<?> addDepartment(@RequestParam String name){
        return new ResponseEntity<>(departmentService.addNewDepartment(name), HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/swap-user-departments")
    public ResponseEntity<?> swapDepartments(@RequestParam long firstUserId, @RequestParam long secondUserId){
        try {
            departmentService.swapUserDepartments(firstUserId, secondUserId);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Отделы заменены!", HttpStatus.OK);
    }
}
