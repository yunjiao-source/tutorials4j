package tutorials4j.framework.examples.hibernate.secondlevelcache;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 员工数据访问接口。
 *
 * <p>继承 Spring Data JPA 的 {@link JpaRepository}，提供对 {@link Employee} 实体的
 * 基础增删改查能力；如需自定义查询方法，可在本接口中声明。
 *
 * @author Yun Jiao
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  // 可添加自定义查询方法
}
