package test.SpringProject.Repositories;

import org.springframework.data.repository.CrudRepository;
import test.SpringProject.Entities.Department;

public interface DepartmentRepo extends CrudRepository<Department, Long> {
}
