package tutorials4j.framework.examples.jpa.table;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户数据仓库接口。
 *
 * <p>继承 {@link JpaRepository} 提供用户实体的基础数据访问能力。
 *
 * @author Yun Jiao
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  /**
   * 根据用户名查询用户。
   *
   * @param username 用户名
   * @return 匹配的用户，若不存在返回 {@code null}
   */
  User findByName(String username);
}
