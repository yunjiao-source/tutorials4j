package tutorials4j.framework.examples.jpa.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 仓库
 *
 * @author Yun Jiao
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String username);
}
