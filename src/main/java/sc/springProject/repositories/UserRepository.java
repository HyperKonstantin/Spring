package sc.springProject.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

     List<User> findByName(String name);

    List<User> findByDepartment(Department department);

    List<User> findByNameIsStartingWith(String name);

    Optional<User> findById(long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findFirstById(long id);

}
