package tutorials4j.feature.basic.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
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

  @EntityGraph(attributePaths = "dictDataList")
  @Query("select t1 from DictTypeEntity t1")
  List<DictTypeEntity> findAllAndFetch();
}
