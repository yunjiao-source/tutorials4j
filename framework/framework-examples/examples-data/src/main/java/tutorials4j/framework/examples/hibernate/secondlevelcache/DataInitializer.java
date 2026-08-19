package tutorials4j.framework.examples.hibernate.secondlevelcache;

import com.github.javafaker.Faker;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 二级缓存演示的初始数据填充器。
 *
 * <p>应用启动后执行：若 {@code data_employees} 表中数据不足 5 条，则使用 Faker 生成 5 条员工数据写入数据库，用于演示 Hibernate 二级缓存的效果。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final EmployeeRepository employeeRepository;
  private final Faker faker = new Faker();

  /**
   * 应用启动时填充演示数据。
   *
   * @param args 命令行参数
   */
  @Override
  public void run(String... args) {
    List<Employee> employees = employeeRepository.findAll();
    if (employees.size() >= 5) {
      return;
    }

    String[] department = new String[] {"A", "B"};
    IntStream.range(0, 5)
        .forEach(
            i -> {
              Employee data = new Employee();
              data.setName(faker.name().username());
              data.setDepartment(faker.options().option(department));

              employeeRepository.save(data);
            });
  }
}
