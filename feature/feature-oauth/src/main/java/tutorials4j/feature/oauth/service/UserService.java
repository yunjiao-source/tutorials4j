package tutorials4j.feature.oauth.service;

import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tutorials4j.feature.oauth.entity.UserEntity;
import tutorials4j.feature.oauth.exception.OAuthFeatureErrorCode;
import tutorials4j.feature.oauth.model.UserStatus;
import tutorials4j.feature.oauth.repository.UserRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Service
@RequiredArgsConstructor
public class UserService implements BaseService<UserEntity, String> {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public BaseRepository<UserEntity, String> getRepository() {
    return userRepository;
  }

  public UserEntity findByUsername(String username) {
    Assert.hasText("username", "username must not be null or empty");
    return userRepository
        .findByUsername(username)
        .orElseThrow(OAuthFeatureErrorCode.ACCOUNT_NOT_FOUND::throwed);
  }

  public UserEntity authenticate(String username, String password) {
    Assert.hasText("username", "username must not be null or empty");
    Assert.hasText("password", "password must not be null or empty");

    UserEntity entity =
        userRepository
            .findByUsername(username)
            .orElseThrow(OAuthFeatureErrorCode.LOGIN_FAIL::throwed);

    if (!Objects.equals(UserStatus.active, entity.getStatus())) {
      throw OAuthFeatureErrorCode.ACCOUNT_NOT_ACTIVE.throwed();
    }

    Instant now = Instant.now();
    if (entity.getAccountExpireAt() != null
        && entity.getAccountExpireAt().getEpochSecond() < now.getEpochSecond()) {
      throw OAuthFeatureErrorCode.ACCOUNT_EXPIRED.throwed();
    }
    if (entity.getCredentialsExpireAt() != null
        && entity.getCredentialsExpireAt().getEpochSecond() < now.getEpochSecond()) {
      throw OAuthFeatureErrorCode.ACCOUNT_PASSWORD_EXPIRED.throwed();
    }
    if (!passwordEncoder.matches(password, entity.getPassword())) {
      throw OAuthFeatureErrorCode.LOGIN_FAIL.throwed();
    }
    return entity;
  }
}
