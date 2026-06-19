package tutorials4j.framework.examples.hibernate.secondlevelcache;

import com.github.javafaker.Faker;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final EmployeeRepository employeeRepository;
  private final Faker faker = new Faker();

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
