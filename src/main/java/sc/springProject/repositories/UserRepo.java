package sc.springProject.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

     List<User> findByName(String name);

    List<User> findByDepartment(Department department);

    @Modifying
    @Query("UPDATE User SET department = ?2 WHERE id = ?1")
    void changeDepartment(@Param("userId") long userId, @Param("department") Department department);

    @Modifying
    @Query("UPDATE User SET name = ?2 WHERE id = ?1")
    void changeName(long userId, String name);

    List<User> findByNameIsStartingWith(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findById(long id);

}
