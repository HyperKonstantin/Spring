package sc.springProject.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sc.springProject.dto.UserDto;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;
import sc.springProject.repositories.DepartmentRepository;
import sc.springProject.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DepartmentService {

    private DepartmentRepository departmentRepository;
    private UserRepository userRepository;

    private DtoMapper dtoMapper;

    public List<Department> getAllDepartments(){
        return departmentRepository.findAll();
    }

    public List<UserDto> getAllUsersFromDepartment(long departmentId){
        Optional<Department> department = departmentRepository.findById(departmentId);

        if (department.isEmpty()) {
            return null;
        }

        List<User> users = userRepository.findByDepartment(department.get());
        return users.stream().map(dtoMapper::mapToUserDto).toList();
    }

    public void addNewDepartment(Department department){
        departmentRepository.save(department);
    }

    public void swapUserDepartments(long firstUserId, long secondUserId){
        Optional<User> firstUserOptional = userRepository.findById(firstUserId);
        Optional<User> secondUserOptional = userRepository.findById(secondUserId);

        if (firstUserOptional.isEmpty() || secondUserOptional.isEmpty()){
            throw new IllegalArgumentException("Пользователя с таким id не существует!");
        }

        User firstUser = firstUserOptional.get();
        User secondUser = secondUserOptional.get();

        firstUser.setDepartment(secondUser.getDepartment());
        secondUser.setDepartment(firstUser.getDepartment());

        userRepository.save(firstUser);
        userRepository.save(secondUser);
    }
}
