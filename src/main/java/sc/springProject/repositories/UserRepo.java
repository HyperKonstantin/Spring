package sc.springProject.repositories;

import org.springframework.data.repository.CrudRepository;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;

import java.util.List;

public interface UserRepo extends CrudRepository<User, Long> {

    List<User> findByName(String name);

    List<User> findByDepartment(Department department);
}
