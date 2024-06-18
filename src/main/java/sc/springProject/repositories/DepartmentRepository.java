package sc.springProject.repositories;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sc.springProject.entities.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Department findFirstById(long id);

}
