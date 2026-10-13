package tutorials4j.feature.oauth.service;

import static tutorials4j.feature.oauth.OAuthFeatureConsts.CACHE_NAME_USERS;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import tutorials4j.feature.oauth.autoconfigure.OAuthFeatureProperties;
import tutorials4j.feature.oauth.component.LoginCounterComponent;
import tutorials4j.feature.oauth.entity.UserEntity;
import tutorials4j.feature.oauth.exception.OAuthFeatureErrorCode;
import tutorials4j.feature.oauth.model.UserCreateModel;
import tutorials4j.feature.oauth.model.UserUpdateModel;
import tutorials4j.feature.oauth.repository.UserRepository;
import tutorials4j.feature.oauth.util.ConverterUtils;
import tutorials4j.toolkit.core.model.UserModel;
import tutorials4j.toolkit.core.model.UserStatus;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseService;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Service
@Validated
@RequiredArgsConstructor
public class UserService implements BaseService<UserEntity, String> {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final OAuthFeatureProperties properties;
  private final LoginCounterComponent loginCounterComponent;

  @Override
  public BaseRepository<UserEntity, String> getRepository() {
    return userRepository;
  }

  public UserEntity create(@Valid UserCreateModel model) {
    Assert.notNull(model, "model must not be null");
    var entity = new UserEntity();
    BeanUtils.copyProperties(model, entity);

    Instant now = Instant.now();
    entity.setPassword(passwordEncoder.encode(model.getPassword()));
    if (entity.getCredentialsExpireAt() == null) {
      entity.setCredentialsExpireAt(now.plus(properties.getDefaultCredentialsExpire()));
    }
    if (entity.getAccountExpireAt() == null) {
      entity.setAccountExpireAt(now.plus(properties.getDefaultAccountExpire()));
    }
    if (entity.getStatus() == null) {
      entity.setStatus(UserStatus.active);
    }
    return userRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public UserEntity update(String id, @Valid UserUpdateModel model) {
    Assert.notNull(model, "model must not be null");

    var entity = findById(id);
    BeanUtils.copyProperties(model, entity);
    return userRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    userRepository.findById(id).ifPresent(userRepository::delete);
  }

  public UserEntity findByUsername(String username) {
    Assert.hasText("username", "username must not be null or empty");
    return userRepository
        .findByUsername(username)
        .orElseThrow(OAuthFeatureErrorCode.ACCOUNT_NOT_FOUND::throwed);
  }

  public void authenticate(UserModel userModel, String password) {
    Assert.hasText("userModel", "userModel must not be null or empty");
    Assert.hasText("password", "password must not be null or empty");

    if (!Objects.equals(UserStatus.active, userModel.getStatus())) {
      throw OAuthFeatureErrorCode.AUTH_FAIL.throwed("账户非活动状态");
    }

    Instant now = Instant.now();
    if (userModel.getAccountExpireAt() != null
        && userModel.getAccountExpireAt().getEpochSecond() < now.getEpochSecond()) {
      throw OAuthFeatureErrorCode.AUTH_FAIL.throwed("账户已过期");
    }
    if (userModel.getCredentialsExpireAt() != null
        && userModel.getCredentialsExpireAt().getEpochSecond() < now.getEpochSecond()) {
      throw OAuthFeatureErrorCode.AUTH_FAIL.throwed("密码已过期");
    }

    if (loginCounterComponent.check(userModel.getUsername())) {
      // 超过失败次数
      throw OAuthFeatureErrorCode.TOO_MANY_LOGIN_FAIL.throwed();
    }

    if (!passwordEncoder.matches(password, userModel.getPassword())) {
      loginCounterComponent.counting(userModel.getUsername());
      throw OAuthFeatureErrorCode.AUTH_FAIL.throwed("用户名或密码错误");
    }
  }

  @Cacheable(value = CACHE_NAME_USERS, key = "#username")
  public UserModel findUserModelByUserName(String username) {
    Assert.hasText("username", "username must not be null or empty");
    return userRepository
        .findWithRolesAndPermissionsByUsername(username)
        .map(e -> ConverterUtils.getInstance().convertUserEntity.apply(e))
        .orElseThrow(OAuthFeatureErrorCode.ACCOUNT_NOT_FOUND::throwed);
  }
}
