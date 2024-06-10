package sc.springProject.repositories;

import org.springframework.data.repository.CrudRepository;
import sc.springProject.entities.Department;

public interface DepartmentRepo extends CrudRepository<Department, Long> {
}
