package tutorials4j.feature.oauth.service;

import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tutorials4j.feature.oauth.entity.OpenUserEntity;
import tutorials4j.feature.oauth.model.OpenIdCreateModel;
import tutorials4j.feature.oauth.model.UnionIdCreateModel;
import tutorials4j.feature.oauth.repository.OpenUserRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class OpenUserService implements BaseService<OpenUserEntity, String> {
  private final OpenUserRepository openUserRepository;

  @Override
  public BaseRepository<OpenUserEntity, String> getRepository() {
    return openUserRepository;
  }

  @Transactional(rollbackFor = Exception.class)
  public synchronized OpenUserEntity createOpenId(OpenIdCreateModel model) {
    Assert.notNull(model, "model must not be null");
    model.validate();

    Optional<OpenUserEntity> openUserEntityOptional =
        openUserRepository.findByUserIdAndClientId(model.getUserId(), model.getClientId());
    if (openUserEntityOptional.isPresent()) {
      return openUserEntityOptional.get();
    }

    OpenUserEntity entity = new OpenUserEntity();
    entity.setUserId(model.getUserId());
    entity.setClientId(model.getClientId());
    entity.setPrefix(model.getPrefix());
    entity.setOpenId(model.getOpenId());
    entity.setAttrMap(model.getAttrMap());
    entity.setCreateTime(Instant.now());
    return openUserRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public synchronized OpenUserEntity createUnionId(UnionIdCreateModel model) {
    Assert.notNull(model, "model must not be null");
    model.validate();

    Optional<OpenUserEntity> openUserEntityOptional =
        openUserRepository.findByUserIdAndSubjectId(model.getUserId(), model.getSubjectId());
    if (openUserEntityOptional.isPresent()) {
      return openUserEntityOptional.get();
    }

    OpenUserEntity entity = new OpenUserEntity();
    entity.setUserId(model.getUserId());
    entity.setSubjectId(model.getSubjectId());
    entity.setPrefix(model.getPrefix());
    entity.setUnionId(model.getUnionId());
    entity.setAttrMap(model.getAttrMap());
    entity.setCreateTime(Instant.now());
    return openUserRepository.save(entity);
  }
}
