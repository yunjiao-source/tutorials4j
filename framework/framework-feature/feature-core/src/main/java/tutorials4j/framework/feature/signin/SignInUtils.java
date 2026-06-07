package tutorials4j.framework.feature.signin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class SignInUtils {
  // 日活key前缀
  private static final String DAU_KEY_PREFIX = "dau:";
  // 月活
  private static final String MAU_KEY_PREFIX = "mau:";

  private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
  private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

  private SignInUtils() {}

  public static String monthlyKey(String keyPrefix, String source, String account, LocalDate date) {
    return keyPrefix + source + ":" + account + ":" + date.format(MONTH_FORMATTER);
  }

  public static String dauKey(String keyPrefix, String source, LocalDate date) {
    return keyPrefix + source + ":" + DAU_KEY_PREFIX + date.format(DAY_FORMATTER);
  }

  public static String mauKey(String keyPrefix, String source, LocalDate date) {
    return keyPrefix + source + ":" + MAU_KEY_PREFIX + date.format(MONTH_FORMATTER);
  }

  public static int offset(LocalDate date) {
    return date.getDayOfMonth() - 1;
  }
}
