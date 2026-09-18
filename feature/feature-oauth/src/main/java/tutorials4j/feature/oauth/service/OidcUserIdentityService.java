package tutorials4j.feature.oauth.service;

import java.time.Instant;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tutorials4j.feature.oauth.entity.OidcUserIdentityEntity;
import tutorials4j.feature.oauth.model.OidcUserOpenIdCreateModel;
import tutorials4j.feature.oauth.model.OidcUserUnionIdCreateModel;
import tutorials4j.feature.oauth.repository.OidcUserIdentityRepository;
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
public class OidcUserIdentityService implements BaseService<OidcUserIdentityEntity, String> {
  private final OidcUserIdentityRepository oidcUserIdentityRepository;

  @Override
  public BaseRepository<OidcUserIdentityEntity, String> getRepository() {
    return oidcUserIdentityRepository;
  }

  @Transactional(rollbackFor = Exception.class)
  public synchronized OidcUserIdentityEntity createOpenId(OidcUserOpenIdCreateModel model) {
    Assert.notNull(model, "model must not be null");
    model.validate();

    Optional<OidcUserIdentityEntity> openUserEntityOptional =
        oidcUserIdentityRepository.findByUserIdAndClientId(model.getUserId(), model.getClientId());
    if (openUserEntityOptional.isPresent()) {
      return openUserEntityOptional.get();
    }

    OidcUserIdentityEntity entity = new OidcUserIdentityEntity();
    entity.setUserId(model.getUserId());
    entity.setClientId(model.getClientId());
    entity.setPrefix(model.getPrefix());
    entity.setOpenId(model.getOpenId());
    entity.setAttrMap(model.getAttrMap());
    entity.setCreateTime(Instant.now());
    return oidcUserIdentityRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public synchronized OidcUserIdentityEntity createUnionId(OidcUserUnionIdCreateModel model) {
    Assert.notNull(model, "model must not be null");
    model.validate();

    Optional<OidcUserIdentityEntity> openUserEntityOptional =
        oidcUserIdentityRepository.findByUserIdAndSubjectId(
            model.getUserId(), model.getSubjectId());
    if (openUserEntityOptional.isPresent()) {
      return openUserEntityOptional.get();
    }

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
