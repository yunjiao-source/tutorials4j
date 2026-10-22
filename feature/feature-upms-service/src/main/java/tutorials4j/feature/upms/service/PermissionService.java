package tutorials4j.feature.upms.service;

import jakarta.validation.Valid;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tutorials4j.feature.upms.entity.PermissionEntity;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.feature.upms.model.PermissionCreateDTO;
import tutorials4j.feature.upms.model.PermissionUpdateDTO;
import tutorials4j.feature.upms.repository.PermissionRepository;
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
public class PermissionService implements BaseService<PermissionEntity, String> {
  private final PermissionRepository permissionRepository;

  @Override
  public BaseRepository<PermissionEntity, String> getRepository() {
    return permissionRepository;
  }

  @Transactional(rollbackFor = Exception.class)
  public Set<RoleEntity> findRolesById(String parentId) {
    PermissionEntity entity = findById(parentId);
    return entity.getRoles();
  }

  public PermissionEntity create(@Valid PermissionCreateDTO dto) {
    Assert.notNull(dto, "dto must not be null");
    PermissionEntity entity = new PermissionEntity();
    BeanUtils.copyProperties(dto, entity);

    return permissionRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public PermissionEntity update(String id, @Valid PermissionUpdateDTO dto) {
    Assert.notNull(dto, "dto must not be null");

    PermissionEntity entity = findById(id);
    BeanUtils.copyProperties(dto, entity);
    return permissionRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    permissionRepository.findById(id).ifPresent(permissionRepository::delete);
  }
}
