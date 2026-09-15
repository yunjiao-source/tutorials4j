package tutorials4j.microservice.demo.component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.microservice.demo.BookEntity;
import tutorials4j.microservice.demo.repository.BookRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class BookDataInitializer implements CommandLineRunner {

  private final BookRepository bookRepository;

  @Override
  public void run(String... args) {
    if (bookRepository.count() > 0) {
      return;
    }
    bookRepository.saveAll(
        List.of(
            new BookEntity(
                "Java 编程思想", "Bruce Eckel", new BigDecimal("108.00"), LocalDate.of(2007, 6, 1)),
            new BookEntity(
                "Spring 实战", "Craig Walls", new BigDecimal("89.00"), LocalDate.of(2022, 3, 1)),
            new BookEntity(
                "深入理解 JVM 虚拟机", "周志明", new BigDecimal("129.00"), LocalDate.of(2019, 12, 1)),
            new BookEntity(
                "Effective Java",
                "Joshua Bloch",
                new BigDecimal("99.00"),
                LocalDate.of(2018, 1, 1))));
    System.out.println(">>> 初始化图书数据完成，共 " + bookRepository.count() + " 条");
  }
}
