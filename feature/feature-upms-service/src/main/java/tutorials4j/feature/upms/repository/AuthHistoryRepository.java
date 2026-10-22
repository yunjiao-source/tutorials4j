package tutorials4j.feature.upms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import tutorials4j.feature.upms.entity.AuthHistoryEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Repository
public interface AuthHistoryRepository extends BaseRepository<AuthHistoryEntity, String> {

  @Override
  @EntityGraph(attributePaths = {"user"})
  Page<AuthHistoryEntity> findAll(Specification<AuthHistoryEntity> spec, Pageable pageable);
}
