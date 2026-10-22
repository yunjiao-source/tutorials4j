package tutorials4j.feature.basic.repository;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.basic.entity.DictTypeEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface DictTypeRepository extends BaseRepository<DictTypeEntity, String> {

  Optional<DictTypeEntity> findByCode(String code);
}
