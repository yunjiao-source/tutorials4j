package tutorials4j.feature.upms.util;

import static tutorials4j.toolkit.core.constant.ToolkitConsts.AUTHORITY_ROLE_PREFIX;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import org.springframework.beans.BeanUtils;
import tutorials4j.feature.upms.entity.PermissionEntity;
import tutorials4j.feature.upms.entity.RoleEntity;
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
        Set<String> authorities = new HashSet<>();
        Set<RoleEntity> roles = userEntity.getRoles();
        for (RoleEntity role : roles) {
          authorities.add(AUTHORITY_ROLE_PREFIX + role.getCode());
          Set<PermissionEntity> permissions = role.getPermissions();
          for (PermissionEntity permission : permissions) {
            authorities.add(permission.getCode());
          }
        }

        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userEntity, userModel);
        userModel.setUserId(userEntity.getId());
        userModel.setAuthorities(authorities);

        return userModel;
      };
}
