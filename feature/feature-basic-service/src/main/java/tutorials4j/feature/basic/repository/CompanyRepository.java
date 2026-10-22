package tutorials4j.feature.basic.repository;

import static tutorials4j.feature.basic.BasicFeatureConsts.CACHEABLE_COMPANY;

import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.basic.entity.CompanyEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface CompanyRepository extends BaseRepository<CompanyEntity, String> {

  @Cacheable(value = CACHEABLE_COMPANY, key = "#organizeId")
  Optional<CompanyEntity> findByOrganizeId(String organizeId);
}
