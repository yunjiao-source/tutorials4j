package tutorials4j.framework.feature.signin.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 签到工具类
 *
 * <p>提供签到相关 Redis 键的拼接（月度签到键、日活键、月活键）与签到日期偏移计算等静态工具方法。
 *
 * @author Yun Jiao
 */
public class SignInUtils {
  // 日活key前缀
  /** 日活（DAU）键前缀 */
  private static final String DAU_KEY_PREFIX = "dau:";

  // 月活
  /** 月活（MAU）键前缀 */
  private static final String MAU_KEY_PREFIX = "mau:";

  /** 月度格式化器（yyyyMM） */
  private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

  /** 日度格式化器（yyyyMMdd） */
  private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

  /** 私有构造器，禁止实例化 */
  private SignInUtils() {}

  /**
   * 拼接指定账号在指定日期所属月份的月度签到键
   *
   * @param keyPrefix Redis 键前缀
   * @param source 签到来源标识
   * @param account 签到账号
   * @param date 签到日期
   * @return 月度签到键
   */
  public static String monthlyKey(String keyPrefix, String source, String account, LocalDate date) {
    return keyPrefix + source + ":" + account + ":" + date.format(MONTH_FORMATTER);
  }

  /**
   * 拼接指定日期的日活（DAU）键
   *
   * @param keyPrefix Redis 键前缀
   * @param source 签到来源标识
   * @param date 日期
   * @return 日活键
   */
  public static String dauKey(String keyPrefix, String source, LocalDate date) {
    return keyPrefix + source + ":" + DAU_KEY_PREFIX + date.format(DAY_FORMATTER);
  }

  /**
   * 拼接指定日期所属月份的月活（MAU）键
   *
   * @param keyPrefix Redis 键前缀
   * @param source 签到来源标识
   * @param date 日期
   * @return 月活键
   */
  public static String mauKey(String keyPrefix, String source, LocalDate date) {
    return keyPrefix + source + ":" + MAU_KEY_PREFIX + date.format(MONTH_FORMATTER);
  }

  /**
   * 计算指定日期在月度位图中的偏移量（从 0 开始）
   *
   * @param date 日期
   * @return 位图偏移量
   */
  public static int offset(LocalDate date) {
    return date.getDayOfMonth() - 1;
  }
}
