package tutorials4j.feature.basic.service;

import static tutorials4j.feature.basic.BasicFeatureConsts.CACHEABLE_DICT_DATA;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import tutorials4j.feature.basic.entity.DictDataEntity;
import tutorials4j.feature.basic.model.DictDataDTO;
import tutorials4j.feature.basic.model.DictDataVO;
import tutorials4j.feature.basic.repository.DictDataRepository;
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
public class DictDataService implements BaseService<DictDataEntity, String> {
  private final DictDataRepository dictDataRepository;

  @Override
  public BaseRepository<DictDataEntity, String> getRepository() {
    return dictDataRepository;
  }

  public DictDataEntity create(@Valid DictDataDTO dto) {
    Assert.notNull(dto, "dto must not be null");
    DictDataEntity entity = new DictDataEntity();
    BeanUtils.copyProperties(dto, entity);

    return dictDataRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public DictDataEntity update(String id, @Valid DictDataDTO dto) {
    Assert.notNull(dto, "dto must not be null");

    DictDataEntity entity = findById(id);
    BeanUtils.copyProperties(dto, entity);
    return dictDataRepository.save(entity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    dictDataRepository.findById(id).ifPresent(dictDataRepository::delete);
  }

  @Cacheable(value = CACHEABLE_DICT_DATA, key = "#dictTypeId")
  public List<DictDataVO> findByDictTypeIdAndEnabledIn(
      String dictTypeId, Set<YesNoEnum> allEnabled) {
    List<DictDataVO> dictDataVOList;

    List<DictDataEntity> dictDataEntities = dictDataRepository.findByDictTypeId(dictTypeId);
    if (CollectionUtils.sizeIsEmpty(allEnabled)) {
      dictDataVOList = DictDataVO.of(dictDataEntities);
    } else {
      dictDataVOList =
          dictDataEntities.stream()
              .filter(e -> allEnabled.contains(e.getEnabled()))
              .map(DictDataVO::of)
              .toList();
    }

    return new ArrayList<>(dictDataVOList);
  }
}
