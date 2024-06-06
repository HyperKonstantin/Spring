package sc.SpringProject.Repositories;

import org.springframework.data.repository.CrudRepository;
import sc.SpringProject.Entities.Department;

public interface DepartmentRepo extends CrudRepository<Department, Long> {
}
