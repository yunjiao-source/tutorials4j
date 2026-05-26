package tutorials4j.springboot3.batch.simple.user;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

/**
 * 用户数据验证器
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class UserCsvValidator implements Validator<UserCsvRecord> {
  private final int nameMinLength;
  private final int nameMaxLength;
  private final String emailPattern;

  @Override
  public void validate(UserCsvRecord item) throws ValidationException {
    if (item.getName() == null || item.getName().trim().isEmpty()) {
      throw new ValidationException("Name cannot be empty");
    }
    if (item.getName().length() < nameMinLength || item.getName().length() > nameMaxLength) {
      throw new ValidationException(
          String.format(
              "Name length must be between %d and %d characters", nameMinLength, nameMaxLength));
    }
    if (item.getEmail() == null || !item.getEmail().matches(emailPattern)) {
      throw new ValidationException("Invalid email format");
    }
  }
}
