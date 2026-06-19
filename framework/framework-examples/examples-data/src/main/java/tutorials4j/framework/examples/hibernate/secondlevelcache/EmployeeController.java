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

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

  @Autowired private EmployeeService employeeService;

  @GetMapping("/{id}")
  public Employee getEmployee(@PathVariable Long id) {
    return employeeService.getEmployeeById(id);
  }

  @GetMapping
  public List<Employee> getAllEmployees() {
    return employeeService.getAllEmployees();
  }

  @PostMapping
  public Employee createEmployee(@RequestBody Employee employee) {
    return employeeService.saveEmployee(employee);
  }

  @DeleteMapping("/{id}")
  public void deleteEmployee(@PathVariable Long id) {
    employeeService.deleteEmployee(id);
  }

  @PostMapping("/clear-cache")
  public void clearCache() {
    employeeService.clearCache();
  }

  @GetMapping("/stats")
  public void printStats() {
    // 由于是演示，直接打印到控制台，也可以返回JSON
    if (employeeService instanceof EmployeeService) {
      ((EmployeeService) employeeService).printStatistics();
    }
  }
}
