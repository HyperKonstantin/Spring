package sc.springProject.repositories;

import org.springframework.stereotype.Repository;
import sc.springProject.entities.UserView;

import java.util.List;

@Repository
public interface UserViewRepository extends ViewJpaRepository<UserView, Long> {
    List<UserView> findByName(String name);
}
