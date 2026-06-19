package tutorials4j.framework.examples.hibernate.secondlevelcache;

import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

  @Autowired private EmployeeRepository employeeRepository;

  @Autowired private SessionFactory sessionFactory;

  public Employee getEmployeeById(Long id) {
    // 此查询会经过 Hibernate 二级缓存
    return employeeRepository.findById(id).orElse(null);
  }

  public List<Employee> getAllEmployees() {
    // 此查询会触发 N+1 或其他，但 Employee 实体本身会从缓存获取
    // 注意：查询缓存未开启，但实体会从二级缓存读取（如果之前已缓存）
    return employeeRepository.findAll();
  }

  @Transactional
  public Employee saveEmployee(Employee employee) {
    return employeeRepository.save(employee);
  }

  @Transactional
  public void deleteEmployee(Long id) {
    employeeRepository.deleteById(id);
  }

  @Transactional
  public void clearCache() {
    // 清空所有二级缓存（用于测试）
    sessionFactory.getCache().evictAllRegions();
  }

  // 用于打印统计信息（验证缓存命中）
  public void printStatistics() {
    Statistics stats = sessionFactory.getStatistics();
    System.out.println("--------------------------------");
    System.out.println("Second-level cache hit count: " + stats.getSecondLevelCacheHitCount());
    System.out.println("Second-level cache miss count: " + stats.getSecondLevelCacheMissCount());
    System.out.println("Second-level cache put count:  " + stats.getSecondLevelCachePutCount());
    System.out.println("Query cache hit count: " + stats.getQueryCacheHitCount());
    System.out.println("--------------------------------");
  }
}
