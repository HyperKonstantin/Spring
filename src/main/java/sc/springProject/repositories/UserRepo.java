package sc.springProject.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;

import java.util.List;

public interface UserRepo extends CrudRepository<User, Long> {

     List<User> findByName(String name);

    List<User> findByDepartment(Department department);

    @Modifying
    @Query(value = "UPDATE User SET department = :departmentId where id = :userId")
    void changeDepartmentId(@Param("userId") long userId, @Param("departmentId") long departmentId);
}
