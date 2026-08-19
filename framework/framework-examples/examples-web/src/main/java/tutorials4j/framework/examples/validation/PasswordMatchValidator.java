package tutorials4j.framework.examples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * {@link PasswordMatch} 注解的校验器
 *
 * <p>校验 {@link UserForm} 中密码与确认密码是否一致。
 *
 * @author Yun Jiao
 */
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserForm> {
  /** 校验密码与确认密码是否一致，任一为空或两者不同均校验失败 */
  @Override
  public boolean isValid(UserForm form, ConstraintValidatorContext context) {
    if (form.getPassword() == null || form.getConfirmPassword() == null) {
      return false;
    }
    return form.getPassword().equals(form.getConfirmPassword());
  }
}
