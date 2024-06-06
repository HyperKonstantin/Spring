package sc.SpringProject.Repositories;

import org.springframework.data.repository.CrudRepository;
import sc.SpringProject.Entities.Department;
import sc.SpringProject.Entities.User;

import java.util.List;

public interface UserRepo extends CrudRepository<User, Long> {

    List<User> findByName(String name);

    List<User> findByDepartment(Department department);
}
