package tutorials4j.feature.basic.repository;

import static tutorials4j.feature.basic.BasicFeatureConsts.CACHEABLE_DICT_DATA;

import java.util.List;
import java.util.Set;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.basic.entity.DictDataEntity;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface DictDataRepository extends BaseRepository<DictDataEntity, String> {

  @Override
  @EntityGraph(attributePaths = {"dictType"})
  Page<DictDataEntity> findAll(Specification<DictDataEntity> spec, Pageable pageable);

  @Cacheable(value = CACHEABLE_DICT_DATA, key = "#dictTypeId")
  List<DictDataEntity> findByDictTypeIdAndEnabledIn(String dictTypeId, Set<YesNoEnum> allEnabled);

  long countByDictTypeId(String dictTypeId);

  List<DictDataEntity> findByDictTypeId(String dictTypeId);
}
