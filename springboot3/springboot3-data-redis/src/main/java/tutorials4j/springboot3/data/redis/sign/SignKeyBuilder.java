package tutorials4j.springboot3.data.redis.sign;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Key 生成工具
 *
 * @author Yun Jiao
 */
public class SignKeyBuilder {
  private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

  private SignKeyBuilder() {}

  public static String monthlyKey(Long userId, LocalDate date) {
    return "sign:uid:" + userId + ":" + date.format(MONTH_FORMATTER);
  }

  public static int offset(LocalDate date) {
    return date.getDayOfMonth() - 1;
  }
}
