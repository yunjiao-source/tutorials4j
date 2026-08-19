package tutorials4j.framework.examples.signin;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.framework.feature.signin.service.SignInTemplateFactory;

/**
 * 演示数据初始化器，应用启动时向签到服务写入示例签到数据。
 *
 * <p>为 demo_user 生成最近两天的签到记录，并为 demo_user1 生成当月每一天的签到记录。
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DataRunner implements CommandLineRunner {
  /** 演示数据的来源标识。 */
  private static final String SOURCE = "web_app";

  /** 签到模板工厂，用于执行签到。 */
  private final SignInTemplateFactory signInTemplateFactory;

  /**
   * 应用启动时写入演示签到数据。
   *
   * @param args 命令行参数
   * @throws Exception 签到失败时抛出
   */
  @Override
  public void run(String... args) throws Exception {
    LocalDate now = LocalDate.now();

    IntStream.range(1, 3)
        .forEach(
            i -> {
              signInTemplateFactory.template(SOURCE).signIn("demo_user", now.minusDays(i));
            });

    YearMonth yearMonth = YearMonth.now();
    LocalDate firstDay = yearMonth.atDay(1);
    LocalDate lastDay = yearMonth.atEndOfMonth();

    firstDay
        .datesUntil(lastDay.plusDays(1))
        .forEach(date -> signInTemplateFactory.template(SOURCE).signIn("demo_user1", date));
  }
}
