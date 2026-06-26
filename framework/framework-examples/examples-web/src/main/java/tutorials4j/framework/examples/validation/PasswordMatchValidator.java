package tutorials4j.framework.examples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserForm> {
  @Override
  public boolean isValid(UserForm form, ConstraintValidatorContext context) {
    if (form.getPassword() == null || form.getConfirmPassword() == null) {
      return false;
    }
    return form.getPassword().equals(form.getConfirmPassword());
  }
}
