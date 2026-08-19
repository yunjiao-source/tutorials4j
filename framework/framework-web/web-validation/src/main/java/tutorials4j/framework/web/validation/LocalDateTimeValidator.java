package tutorials4j.framework.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;

/**
 * {@link LocalDateTimeFormat} 注解的校验器实现类。
 *
 * <p>用于校验字符串是否为合法的日期、时间或日期时间格式。 空字符串或 null 视为合法（通过校验），非空串才会进行格式检查。
 *
 * <p>校验流程：
 *
 * <ol>
 *   <li>如果字符串为空白或 null，直接返回 true
 *   <li>根据注解中指定的 {@link DateTimeType} 决定解析目标类型：
 *       <ul>
 *         <li>{@link DateTimeType#Date} → 使用 {@link LocalDate#parse(CharSequence,
 *             DateTimeFormatter)}
 *         <li>{@link DateTimeType#Time} → 使用 {@link LocalTime#parse(CharSequence,
 *             DateTimeFormatter)}
 *         <li>{@link DateTimeType#DateTime} → 使用 {@link LocalDateTime#parse(CharSequence,
 *             DateTimeFormatter)}
 *       </ul>
 *   <li>解析成功则返回 true；解析过程中抛出任何异常均返回 false
 * </ol>
 *
 * @author Yun Jiao
 * @see LocalDateTimeFormat
 * @see DateTimeType
 */
public class LocalDateTimeValidator implements ConstraintValidator<LocalDateTimeFormat, String> {

  private String pattern;

  private DateTimeType dateTimeType;

  private DateTimeFormatter formatter;

  /**
   * 初始化校验器：解析注解中配置的格式模式与时间类型，并构建对应的 {@link DateTimeFormatter}。
   *
   * @param constraintAnnotation 校验注解实例
   */
  @Override
  public void initialize(LocalDateTimeFormat constraintAnnotation) {
    this.pattern = constraintAnnotation.pattern();
    this.dateTimeType = constraintAnnotation.dateTimeType();
    this.formatter = DateTimeFormatter.ofPattern(pattern);
  }

  /**
   * 校验字符串是否符合配置的日期时间格式；空白或 null 视为合法（通过校验）。
   *
   * @param object 待校验的字符串
   * @param constraintContext 校验上下文
   * @return 格式合法返回 true，解析失败返回 false
   */
  @Override
  public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
    if (StringUtils.isBlank(object)) {
      return true;
    }

    try {
      if (DateTimeType.Time.equals(dateTimeType)) {
        LocalTime.parse(object, this.formatter);
      } else if (DateTimeType.Date.equals(dateTimeType)) {
        LocalDate.parse(object, this.formatter);
      } else {
        LocalDateTime.parse(object, this.formatter);
      }
      return true;
    } catch (Exception e) {
      // e.printStackTrace();
      return false;
    }
  }
}
