package sc.springProject.repositories;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sc.springProject.entities.Department;

import java.util.List;

@Repository
public interface DepartmentRepo extends JpaRepository<Department, Long> {

    Department findFirstById(long id);

}
