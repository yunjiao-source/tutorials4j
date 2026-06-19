package tutorials4j.framework.examples.hibernate.secondlevelcache;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  // 可添加自定义查询方法
}
