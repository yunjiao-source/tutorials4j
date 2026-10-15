package tutorials4j.feature.upms.util;

import java.util.function.Function;
import org.springframework.beans.BeanUtils;
import tutorials4j.feature.upms.entity.UserEntity;
import tutorials4j.toolkit.core.model.UserModel;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class ConverterUtils {
  private ConverterUtils() {}

  private static class Holder {
    private static final ConverterUtils INSTANCE = new ConverterUtils();
  }

  public static ConverterUtils getInstance() {
    return Holder.INSTANCE;
  }

  public Function<UserEntity, UserModel> convertUserEntity =
      (userEntity) -> {
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userEntity, userModel);
        userModel.setUserId(userEntity.getId());

        return userModel;
      };
}
