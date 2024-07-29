package sc.springProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface ViewJpaRepository<T, K> extends JpaRepository<T, K> {
    List<T> findAll();
}
