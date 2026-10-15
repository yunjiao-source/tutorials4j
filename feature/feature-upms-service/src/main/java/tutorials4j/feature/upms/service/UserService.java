package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.UpmsFeatureConsts.CACHE_NAME_USERS;

import jakarta.validation.Valid;
import java.time.Instant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import tutorials4j.feature.upms.autoconfigure.UpmsFeatureProperties;
import tutorials4j.feature.upms.entity.UserEntity;
import tutorials4j.feature.upms.exception.UpmsFeatureErrorCode;
import tutorials4j.feature.upms.model.UserCreateModel;
import tutorials4j.feature.upms.model.UserUpdateModel;
import tutorials4j.feature.upms.repository.UserRepository;
import tutorials4j.feature.upms.util.ConverterUtils;
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
  private final UpmsFeatureProperties properties;

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
      entity.setCredentialsExpireAt(now.plus(properties.getCredentialsExpire()));
    }
    if (entity.getAccountExpireAt() == null) {
      entity.setAccountExpireAt(now.plus(properties.getAccountExpire()));
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
        .orElseThrow(UpmsFeatureErrorCode.ACCOUNT_NOT_FOUND::throwed);
  }

  @Cacheable(value = CACHE_NAME_USERS, key = "#username")
  public UserModel findUserModelByUserName(String username) {
    Assert.hasText("username", "username must not be null or empty");
    return userRepository
        .findWithRolesAndPermissionsByUsername(username)
        .map(e -> ConverterUtils.getInstance().convertUserEntity.apply(e))
        .orElseThrow(UpmsFeatureErrorCode.ACCOUNT_NOT_FOUND::throwed);
  }
}
