package tutorials4j.framework.examples.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * 确认密码与密码一致性校验注解
 *
 * <p>标注在表单类型上，用于校验确认密码与密码是否一致，由 {@link PasswordMatchValidator} 提供校验逻辑。
 *
 * @author Yun Jiao
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
@Documented
public @interface PasswordMatch {
  /** 校验失败时的提示信息 */
  String message() default "确认密码与密码不一致";

  /** 校验分组 */
  Class<?>[] groups() default {};

  /** 校验负载 */
  Class<? extends Payload>[] payload() default {};
}
