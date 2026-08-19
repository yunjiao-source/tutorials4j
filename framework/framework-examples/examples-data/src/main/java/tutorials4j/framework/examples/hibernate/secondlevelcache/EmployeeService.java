package tutorials4j.framework.examples.hibernate.secondlevelcache;

import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 员工服务，演示 Hibernate 二级缓存的行为。
 *
 * <p>默认在只读事务中提供员工查询，并通过 {@link SessionFactory} 的统计信息 展示二级缓存与查询缓存的命中情况；写操作（保存、删除、清空缓存）单独开启事务。
 *
 * @author Yun Jiao
 */
@Service
@Transactional(readOnly = true)
public class EmployeeService {

  /** 员工数据访问接口 */
  @Autowired private EmployeeRepository employeeRepository;

  /** Hibernate 会话工厂，用于访问二级缓存与统计信息 */
  @Autowired private SessionFactory sessionFactory;

  /**
   * 根据 id 查询员工，此查询会经过 Hibernate 二级缓存。
   *
   * @param id 员工 id
   * @return 员工实体；不存在时返回 null
   */
  public Employee getEmployeeById(Long id) {
    // 此查询会经过 Hibernate 二级缓存
    return employeeRepository.findById(id).orElse(null);
  }

  /**
   * 查询全部员工列表，实体本身会从二级缓存获取（如果之前已缓存）。
   *
   * @return 员工列表
   */
  public List<Employee> getAllEmployees() {
    // 此查询会触发 N+1 或其他，但 Employee 实体本身会从缓存获取
    // 注意：查询缓存未开启，但实体会从二级缓存读取（如果之前已缓存）
    return employeeRepository.findAll();
  }

  /**
   * 保存员工。
   *
   * @param employee 员工实体
   * @return 保存后的员工
   */
  @Transactional
  public Employee saveEmployee(Employee employee) {
    return employeeRepository.save(employee);
  }

  /**
   * 根据 id 删除员工。
   *
   * @param id 员工 id
   */
  @Transactional
  public void deleteEmployee(Long id) {
    employeeRepository.deleteById(id);
  }

  /** 清空所有二级缓存区域（用于测试）。 */
  @Transactional
  public void clearCache() {
    // 清空所有二级缓存（用于测试）
    sessionFactory.getCache().evictAllRegions();
  }

  /** 打印二级缓存与查询缓存的命中/未命中统计信息到控制台（用于验证缓存命中）。 */
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
