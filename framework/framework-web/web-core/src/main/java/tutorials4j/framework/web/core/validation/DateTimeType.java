package tutorials4j.framework.web.core.validation;

/**
 * 日期时间类型枚举，用于标识字符串内容所代表的实际时间类别。
 *
 * <p>与 {@link LocalDateTimeFormat} 注解配合使用，指明应被解析为完整的日期时间、纯日期还是纯时间。
 *
 * @author Yun Jiao
 * @see LocalDateTimeFormat
 * @see LocalDateTimeValidator
 */
public enum DateTimeType {
  /**
   * 日期+时间类型，对应 {@link java.time.LocalDateTime}。
   *
   * <p>示例格式：yyyy-MM-dd HH:mm:ss
   */
  DateTime,

  /**
   * 日期类型，对应 {@link java.time.LocalDate}。
   *
   * <p>示例格式：yyyy-MM-dd
   */
  Date,

  /**
   * 时间类型，对应 {@link java.time.LocalTime}。
   *
   * <p>示例格式：HH:mm:ss
   */
  Time
}
