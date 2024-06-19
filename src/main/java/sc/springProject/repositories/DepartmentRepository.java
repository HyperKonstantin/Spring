package sc.springProject.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sc.springProject.entities.Department;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findById(long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Department> findWithLockingById(long id);
}
