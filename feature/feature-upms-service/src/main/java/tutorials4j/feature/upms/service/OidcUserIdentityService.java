package tutorials4j.feature.upms.service;

import java.time.Instant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tutorials4j.feature.upms.entity.OidcUserIdentityEntity;
import tutorials4j.feature.upms.model.OidcUserOpenIdCreateDTO;
import tutorials4j.feature.upms.model.OidcUserUnionIdCreateDTO;
import tutorials4j.feature.upms.repository.OidcUserIdentityRepository;
import tutorials4j.toolkit.cache.redis.lock.RedisLockable;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseService;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Service
@RequiredArgsConstructor
public class OidcUserIdentityService implements BaseService<OidcUserIdentityEntity, String> {
  private final OidcUserIdentityRepository oidcUserIdentityRepository;

  @Override
  public BaseRepository<OidcUserIdentityEntity, String> getRepository() {
    return oidcUserIdentityRepository;
  }

  @RedisLockable(
      prefix = "openid:",
      key = "#model.userId + '-' + #model.clientId",
      expireTime = 5000L)
  @Transactional(rollbackFor = Exception.class)
  public synchronized OidcUserIdentityEntity createOpenId(OidcUserOpenIdCreateDTO model) {
    Assert.notNull(model, "model must not be null");
    model.validate();

    OidcUserIdentityEntity entity = new OidcUserIdentityEntity();
    entity.setUserId(model.getUserId());
    entity.setClientId(model.getClientId());
    entity.setPrefix(model.getPrefix());
    entity.setOpenId(model.getOpenId());
    entity.setAttrMap(model.getAttrMap());
    entity.setCreateTime(Instant.now());
    return oidcUserIdentityRepository.save(entity);
  }

  @RedisLockable(
      prefix = "unionid:",
      key = "#model.userId + '-' + #model.subjectId",
      expireTime = 5000L)
  @Transactional(rollbackFor = Exception.class)
  public synchronized OidcUserIdentityEntity createUnionId(OidcUserUnionIdCreateDTO model) {
    Assert.notNull(model, "model must not be null");
    model.validate();

    OidcUserIdentityEntity entity = new OidcUserIdentityEntity();
    entity.setUserId(model.getUserId());
    entity.setSubjectId(model.getSubjectId());
    entity.setPrefix(model.getPrefix());
    entity.setUnionId(model.getUnionId());
    entity.setAttrMap(model.getAttrMap());
    entity.setCreateTime(Instant.now());
    return oidcUserIdentityRepository.save(entity);
  }
}
