package tutorials4j.framework.web.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用于校验字符串是否符合指定的日期时间格式的注解。
 * <p>
 * 该注解可应用于字段、方法参数等，配合 Jakarta Bean Validation 框架使用。
 * 通过 {@link LocalDateTimeValidator} 实现具体的校验逻辑，支持日期、时间或日期时间三种类型。
 * </p>
 *
 * <p>示例用法：</p>
 * <pre>{@code
 * public class DemoForm {
 *     @LocalDateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", dateTimeType = DateTimeType.DateTime)
 *     private String createTime;
 *
 *     @LocalDateTimeFormat(pattern = "yyyy-MM-dd", dateTimeType = DateTimeType.Date)
 *     private String birthDate;
 *
 *     @LocalDateTimeFormat(pattern = "HH:mm:ss", dateTimeType = DateTimeType.Time)
 *     private String workStartTime;
 * }
 * }</pre>
 *
 * @author Yun Jiao
 * @see LocalDateTimeValidator
 * @see DateTimeType
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = LocalDateTimeValidator.class)
@Documented
public @interface LocalDateTimeFormat {

    /**
     * 校验失败时返回的默认错误消息键。
     *
     * @return 消息键
     */
    String message() default "{tutorials4j.framework.common.core.validation.LocalDateTimeFormat.message}";

    /**
     * 用于指定校验分组。
     *
     * @return 分组类数组
     */
    Class<?>[] groups() default {};

    /**
     * 有效载荷，用于扩展元数据。
     *
     * @return 有效载荷类数组
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 日期时间格式模式，必须符合 {@link java.time.format.DateTimeFormatter} 的语法规则。
     * <p>例如：<br>
     * 日期时间 - "yyyy-MM-dd HH:mm:ss"<br>
     * 日期 - "yyyy-MM-dd"<br>
     * 时间 - "HH:mm:ss"<br>
     * </p>
     *
     * @return 模式字符串
     */
    String pattern();

    /**
     * 指定待校验的字符串属于哪种时间类型（日期、时间或日期时间），
     * 默认为 {@link DateTimeType#DateTime}。
     *
     * @return 时间类型
     */
    DateTimeType dateTimeType() default DateTimeType.DateTime;

}
