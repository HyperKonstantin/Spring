package sc.SpringProject.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sc.SpringProject.Repositories.DepartmentRepo;
import sc.SpringProject.Entities.Department;
import sc.SpringProject.Entities.User;
import sc.SpringProject.Repositories.UserRepo;

import java.util.List;
import java.util.Optional;

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
}
