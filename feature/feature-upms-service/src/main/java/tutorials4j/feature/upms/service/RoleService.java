package tutorials4j.feature.upms.service;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import tutorials4j.feature.upms.entity.MenuEntity;
import tutorials4j.feature.upms.entity.OrganizeEntity;
import tutorials4j.feature.upms.entity.PermissionEntity;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.feature.upms.model.RoleCreateDTO;
import tutorials4j.feature.upms.model.RoleUpdateDTO;
import tutorials4j.feature.upms.repository.MenuRepository;
import tutorials4j.feature.upms.repository.OrganizeRepository;
import tutorials4j.feature.upms.repository.PermissionRepository;
import tutorials4j.feature.upms.repository.RoleRepository;
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
public class RoleService implements BaseService<RoleEntity, String> {
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final MenuRepository menuRepository;
  private final OrganizeRepository organizeRepository;

  @Override
  public BaseRepository<RoleEntity, String> getRepository() {
    return roleRepository;
  }

  public RoleEntity create(@Valid RoleCreateDTO dto) {
    Assert.notNull(dto, "dto must not be null");
    RoleEntity entity = new RoleEntity();
    BeanUtils.copyProperties(dto, entity);

    return roleRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public RoleEntity update(String id, @Valid RoleUpdateDTO dto) {
    Assert.notNull(dto, "dto must not be null");

    RoleEntity entity = findById(id);
    BeanUtils.copyProperties(dto, entity);
    return roleRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    roleRepository.findById(id).ifPresent(roleRepository::delete);
  }

  @Transactional(rollbackFor = Exception.class)
  public RoleEntity addPermissions(String id, Collection<String> permissions) {
    RoleEntity roleEntity = findById(id);

    Set<PermissionEntity> entities = permissionRepository.findByCodeIn(permissions);
    roleEntity.setPermissions(entities);
    return roleRepository.save(roleEntity);
  }

  public RoleEntity addMenus(String id, Set<String> menus) {
    RoleEntity roleEntity = findById(id);

    List<MenuEntity> entities = menuRepository.findAllById(menus);
    roleEntity.setMenus(new HashSet<>(entities));
    return roleRepository.save(roleEntity);
  }

  public RoleEntity addOrganizes(String id, Set<String> organizes) {
    RoleEntity roleEntity = findById(id);

    List<OrganizeEntity> entities = organizeRepository.findAllById(organizes);
    roleEntity.setOrganizes(entities);
    return roleRepository.save(roleEntity);
  }
}
