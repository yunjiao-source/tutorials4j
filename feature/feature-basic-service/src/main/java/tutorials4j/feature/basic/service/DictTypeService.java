package tutorials4j.feature.basic.service;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import tutorials4j.feature.basic.entity.DictTypeEntity;
import tutorials4j.feature.basic.model.DictTypeDTO;
import tutorials4j.feature.basic.repository.DictTypeRepository;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.exception.DataErrorCode;
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
public class DictTypeService implements BaseService<DictTypeEntity, String> {
  private final DictTypeRepository dictTypeRepository;

  @Override
  public BaseRepository<DictTypeEntity, String> getRepository() {
    return dictTypeRepository;
  }

  public DictTypeEntity create(@Valid DictTypeDTO dto) {
    Assert.notNull(dto, "dto must not be null");
    DictTypeEntity entity = new DictTypeEntity();
    BeanUtils.copyProperties(dto, entity);

    return dictTypeRepository.save(entity);
  }

  public List<DictTypeEntity> findRootByEnabled(Set<YesNoEnum> allEnabled) {
    Assert.notNull(allEnabled, "allEnabled must not be null");

    DictTypeQuery query = DictTypeQuery.builder().allRoot(true).allEnabled(allEnabled).build();
    return findByQuery(query);
  }

  public List<DictTypeEntity> findChildrenByEnabled(String parentId, Set<YesNoEnum> allEnabled) {
    Assert.notNull(allEnabled, "allEnabled must not be null");

    DictTypeQuery query = DictTypeQuery.builder().parentId(parentId).allEnabled(allEnabled).build();
    return findByQuery(query);
  }

  @Transactional(rollbackFor = Exception.class)
  public DictTypeEntity update(String id, @Valid DictTypeDTO dto) {
    Assert.notNull(dto, "dto must not be null");

    DictTypeEntity entity = findById(id);
    BeanUtils.copyProperties(dto, entity);
    return dictTypeRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    dictTypeRepository.findById(id).ifPresent(dictTypeRepository::delete);
  }

  public DictTypeEntity findByCode(String code) {
    return dictTypeRepository
        .findByCode(code)
        .orElseThrow(() -> DataErrorCode.FIND_ENTITY_NOT_EXISTS.throwed().param("code", code));
  }
}
