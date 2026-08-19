package tutorials4j.framework.examples.hibernate.secondlevelcache;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工 REST 接口，演示 Hibernate 二级缓存的读写与统计。
 *
 * <p>提供按 id 查询、列表查询、创建、删除、清空缓存以及打印缓存统计等接口， 路径前缀为 {@code /api/employees}。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

  /** 员工服务 */
  @Autowired private EmployeeService employeeService;

  /**
   * 根据 id 查询员工（会经过二级缓存）。
   *
   * @param id 员工 id
   * @return 员工实体；不存在时返回 null
   */
  @GetMapping("/{id}")
  public Employee getEmployee(@PathVariable Long id) {
    return employeeService.getEmployeeById(id);
  }

  /**
   * 查询全部员工列表。
   *
   * @return 员工列表
   */
  @GetMapping
  public List<Employee> getAllEmployees() {
    return employeeService.getAllEmployees();
  }

  /**
   * 创建员工。
   *
   * @param employee 员工实体
   * @return 创建后的员工
   */
  @PostMapping
  public Employee createEmployee(@RequestBody Employee employee) {
    return employeeService.saveEmployee(employee);
  }

  /**
   * 根据 id 删除员工。
   *
   * @param id 员工 id
   */
  @DeleteMapping("/{id}")
  public void deleteEmployee(@PathVariable Long id) {
    employeeService.deleteEmployee(id);
  }

  /** 清空所有二级缓存区域（用于测试）。 */
  @PostMapping("/clear-cache")
  public void clearCache() {
    employeeService.clearCache();
  }

  /** 打印二级缓存命中/未命中统计信息到控制台。 */
  @GetMapping("/stats")
  public void printStats() {
    // 由于是演示，直接打印到控制台，也可以返回JSON
    if (employeeService instanceof EmployeeService) {
      ((EmployeeService) employeeService).printStatistics();
    }
  }
}
