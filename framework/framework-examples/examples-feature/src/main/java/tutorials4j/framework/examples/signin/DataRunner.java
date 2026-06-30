package tutorials4j.framework.examples.signin;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.framework.feature.signin.service.SignInService;

/**
 * 测试数据
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DataRunner implements CommandLineRunner {
  private static final String SOURCE = "web_app";
  private final SignInService signInService;

  @Override
  public void run(String... args) throws Exception {
    LocalDate now = LocalDate.now();

    IntStream.range(1, 3)
        .forEach(
            i -> {
              signInService.template(SOURCE).signIn("demo_user", now.minusDays(i));
            });

    YearMonth yearMonth = YearMonth.now();
    LocalDate firstDay = yearMonth.atDay(1);
    LocalDate lastDay = yearMonth.atEndOfMonth();

    firstDay
        .datesUntil(lastDay.plusDays(1))
        .forEach(date -> signInService.template(SOURCE).signIn("demo_user1", date));
  }
}
