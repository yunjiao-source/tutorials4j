package tutorials4j.feature.upms.service;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.feature.upms.model.RoleCreateModel;
import tutorials4j.feature.upms.model.RoleUpdateModel;
import tutorials4j.feature.upms.repository.RoleRepository;
import tutorials4j.toolkit.core.model.YesNoEnum;
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
public class RoleService implements BaseService<RoleEntity, String> {
  private final RoleRepository roleRepository;

  @Override
  public BaseRepository<RoleEntity, String> getRepository() {
    return roleRepository;
  }

  public RoleEntity create(@Valid RoleCreateModel model) {
    Assert.notNull(model, "model must not be null");
    var entity = new RoleEntity();
    BeanUtils.copyProperties(model, entity);

    if (entity.getStatus() == null) {
      entity.setStatus(YesNoEnum.yes);
    }
    return roleRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public RoleEntity update(String id, @Valid RoleUpdateModel model) {
    Assert.notNull(model, "model must not be null");

    var entity = findById(id);
    BeanUtils.copyProperties(model, entity);
    return roleRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    roleRepository.findById(id).ifPresent(roleRepository::delete);
  }
}
