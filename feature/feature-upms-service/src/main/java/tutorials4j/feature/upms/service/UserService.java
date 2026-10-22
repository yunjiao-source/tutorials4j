package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.UpmsFeatureConsts.CACHEABLE_USER;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import tutorials4j.feature.core.properties.UpmsFeatureProperties;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.feature.upms.entity.UserEntity;
import tutorials4j.feature.upms.exception.UpmsFeatureErrorCode;
import tutorials4j.feature.upms.model.UserCreateDTO;
import tutorials4j.feature.upms.model.UserUpdateDTO;
import tutorials4j.feature.upms.repository.RoleRepository;
import tutorials4j.feature.upms.repository.UserRepository;
import tutorials4j.feature.upms.util.ConverterUtils;
import tutorials4j.toolkit.core.model.UserModel;
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
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final UpmsFeatureProperties properties;

  @Override
  public BaseRepository<UserEntity, String> getRepository() {
    return userRepository;
  }

  public UserEntity create(@Valid UserCreateDTO model) {
    Assert.notNull(model, "model must not be null");
    UserEntity entity = new UserEntity();
    BeanUtils.copyProperties(model, entity);

    Instant now = Instant.now();
    String password = model.getPassword();
    if (StringUtils.isBlank(password)) {
      password = RandomStringUtils.secure().nextAlphabetic(6);
    }
    entity.setPassword(passwordEncoder.encode(password));
    if (entity.getCredentialsExpireAt() == null) {
      entity.setCredentialsExpireAt(now.plus(properties.getCredentialsExpire()));
    }
    if (entity.getAccountExpireAt() == null) {
      entity.setAccountExpireAt(now.plus(properties.getAccountExpire()));
    }

    return userRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public UserEntity update(String id, @Valid UserUpdateDTO dto) {
    Assert.notNull(dto, "dto must not be null");

    UserEntity entity = findById(id);
    BeanUtils.copyProperties(dto, entity);
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
        .orElseThrow(
            () -> UpmsFeatureErrorCode.ACCOUNT_NOT_FOUND.throwed().param("username", username));
  }

  @Cacheable(value = CACHEABLE_USER, key = "#username")
  public UserModel findUserModelByUserName(String username) {
    UserEntity entity = findByUsername(username);
    return ConverterUtils.getInstance().convertUserEntity.apply(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public UserEntity addRoles(String id, Collection<String> roles) {
    UserEntity entity = findById(id);

    Set<RoleEntity> roleEntities = roleRepository.findByCodeIn(roles);
    entity.setRoles(roleEntities);
    return userRepository.save(entity);
  }
}
