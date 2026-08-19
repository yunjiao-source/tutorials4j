package tutorials4j.framework.examples.jpa.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户数据仓库，提供用户实体的 JPA 持久化操作。
 *
 * @author Yun Jiao
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  /**
   * 根据用户名查询用户。
   *
   * @param username 用户名
   * @return 匹配的用户，不存在时为 null
   */
  User findByName(String username);
}
