package tutorials4j.feature.upms.service;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import tutorials4j.feature.upms.entity.OrganizeEntity;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.feature.upms.exception.UpmsFeatureErrorCode;
import tutorials4j.feature.upms.model.OrganizeDTO;
import tutorials4j.feature.upms.repository.OrganizeRepository;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;
import tutorials4j.toolkit.data.hibernate.domain.BaseService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Service
@Validated
@RequiredArgsConstructor
public class OrganizeService implements BaseService<OrganizeEntity, String> {
  private final OrganizeRepository organizeRepository;

  @Override
  public BaseRepository<OrganizeEntity, String> getRepository() {
    return organizeRepository;
  }

  public List<OrganizeEntity> findAllRoot(Collection<YesNoEnum> allEnabled) {
    return organizeRepository.findAll(
        OrganizeSpecification.allRoot().and(OrganizeSpecification.enabledIn(allEnabled)));
  }

  public List<OrganizeEntity> findAllChild(String parentId, Collection<YesNoEnum> allEnabled) {
    return organizeRepository.findAll(
        OrganizeSpecification.parentIdEqual(parentId)
            .and(OrganizeSpecification.enabledIn(allEnabled)));
  }

  public OrganizeEntity create(@Valid OrganizeDTO dto) {
    Assert.notNull(dto, "dto must not be null");
    OrganizeEntity entity = new OrganizeEntity();
    BeanUtils.copyProperties(dto, entity);
    if (StringUtils.isNotBlank(dto.getParentId())) {
      OrganizeEntity parentEntity = this.fetchById(dto.getParentId());
      entity.setParent(parentEntity);
    }
    return organizeRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public OrganizeEntity update(String id, @Valid OrganizeDTO dto) {
    Assert.notNull(dto, "dto must not be null");

    OrganizeEntity entity = fetchById(id);
    BeanUtils.copyProperties(dto, entity);
    return organizeRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    // 判断是否存在下级组织，有下级不能删除
    OrganizeQuery query = OrganizeQuery.builder().parentId(id).build();
    if (this.count(query) > 0) {
      throw UpmsFeatureErrorCode.ORGANIZE_HAS_CHILDREN_REMOVE_FAIL.throwed().param("parentId", id);
    }
    organizeRepository.findById(id).ifPresent(organizeRepository::delete);
  }

  @Transactional(rollbackFor = Exception.class)
  public List<OrganizeEntity> findChildrenById(String parentId) {
    OrganizeEntity entity = fetchById(parentId);
    return entity.getChildren();
  }

  @Transactional(rollbackFor = Exception.class)
  public Set<RoleEntity> findRolesById(String parentId) {
    OrganizeEntity entity = fetchById(parentId);
    return entity.getRoles();
  }

  public List<OrganizeEntity> findAllValid() {
    OrganizeQuery query = OrganizeQuery.builder().enabled(YesNoEnum.yes).build();
    return findAll(query);
  }
}
