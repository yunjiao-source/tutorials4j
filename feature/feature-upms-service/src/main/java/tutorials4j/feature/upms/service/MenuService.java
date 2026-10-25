package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.service.MenuSpecification.typeNotIn;

import jakarta.validation.Valid;
import java.util.Collection;
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
import tutorials4j.feature.upms.entity.MenuType;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.feature.upms.model.MenuDTO;
import tutorials4j.feature.upms.repository.MenuRepository;
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
@Validated
@RequiredArgsConstructor
public class MenuService implements BaseService<MenuEntity, String> {
  private final MenuRepository menuRepository;
  private final RoleRepository roleRepository;

  @Override
  public BaseRepository<MenuEntity, String> getRepository() {
    return menuRepository;
  }

  public List<MenuEntity> findAllRoot(Collection<YesNoEnum> allEnabled) {
    return menuRepository.findAll(
        MenuSpecification.allRoot().and(MenuSpecification.enabledIn(allEnabled)));
  }

  public List<MenuEntity> findAllChild(String parentId, Collection<YesNoEnum> allEnabled) {
    return menuRepository.findAll(
        MenuSpecification.parentIdEqual(parentId).and(MenuSpecification.enabledIn(allEnabled)));
  }

  @Transactional(rollbackFor = Exception.class)
  public Set<RoleEntity> findRolesById(String parentId) {
    MenuEntity entity = findById(parentId);
    return entity.getRoles();
  }

  @Transactional(rollbackFor = Exception.class)
  public List<MenuEntity> findChildrenById(String parentId) {
    MenuEntity entity = findById(parentId);
    return entity.getChildren();
  }

  public MenuEntity create(@Valid MenuDTO dto) {
    Assert.notNull(dto, "dto must not be null");
    MenuEntity entity = new MenuEntity();
    BeanUtils.copyProperties(dto, entity);

    return menuRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public MenuEntity update(String id, @Valid MenuDTO dto) {
    Assert.notNull(dto, "dto must not be null");

    MenuEntity entity = findById(id);
    BeanUtils.copyProperties(dto, entity);
    return menuRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    menuRepository.findById(id).ifPresent(menuRepository::delete);
  }

  public List<MenuEntity> findRouter() {
    return menuRepository.findAll(typeNotIn(List.of(MenuType.button)));
  }
}
