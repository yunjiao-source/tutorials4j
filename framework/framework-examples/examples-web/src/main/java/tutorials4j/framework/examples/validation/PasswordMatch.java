package tutorials4j.framework.examples.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
@Documented
public @interface PasswordMatch {
  String message() default "确认密码与密码不一致";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
