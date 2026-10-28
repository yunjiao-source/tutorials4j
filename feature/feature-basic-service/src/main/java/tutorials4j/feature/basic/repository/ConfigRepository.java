package tutorials4j.feature.basic.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.basic.entity.ConfigEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface ConfigRepository extends BaseRepository<ConfigEntity, String> {

  Optional<ConfigEntity> findByCode(String code);

  List<ConfigEntity> findByCategory(String category);
}
