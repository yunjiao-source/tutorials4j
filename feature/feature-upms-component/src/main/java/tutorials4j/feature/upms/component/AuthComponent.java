package tutorials4j.feature.upms.component;

import java.time.Instant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import tutorials4j.feature.upms.exception.UpmsFeatureErrorCode;
import tutorials4j.toolkit.core.model.UserModel;
import tutorials4j.toolkit.core.model.UserStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class AuthComponent {
  private final PasswordEncoder passwordEncoder;
  private final LoginCounterComponent loginCounterComponent;

  public void authenticate(UserModel userModel, String password) {
    Assert.hasText("userModel", "userModel must not be null or empty");
    Assert.hasText("password", "password must not be null or empty");

    if (!Objects.equals(UserStatus.active, userModel.getStatus())) {
      throw UpmsFeatureErrorCode.AUTH_FAIL.throwed("账户非活动状态");
    }

    Instant now = Instant.now();
    if (userModel.getAccountExpireAt() != null
        && userModel.getAccountExpireAt().getEpochSecond() < now.getEpochSecond()) {
      throw UpmsFeatureErrorCode.AUTH_FAIL.throwed("账户已过期");
    }
    if (userModel.getCredentialsExpireAt() != null
        && userModel.getCredentialsExpireAt().getEpochSecond() < now.getEpochSecond()) {
      throw UpmsFeatureErrorCode.AUTH_FAIL.throwed("密码已过期");
    }

    if (loginCounterComponent.check(userModel.getUsername())) {
      // 超过失败次数
      throw UpmsFeatureErrorCode.TOO_MANY_LOGIN_FAIL.throwed();
    }

    if (!passwordEncoder.matches(password, userModel.getPassword())) {
      loginCounterComponent.counting(userModel.getUsername());
      throw UpmsFeatureErrorCode.AUTH_FAIL.throwed("用户名或密码错误");
    }
  }
}
