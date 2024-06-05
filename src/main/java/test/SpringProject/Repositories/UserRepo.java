package test.SpringProject.Repositories;

import org.springframework.data.repository.CrudRepository;
import test.SpringProject.Entities.User;

import java.util.List;

public interface UserRepo extends CrudRepository<User, Long> {
    List<User> findByName(String name);
}
