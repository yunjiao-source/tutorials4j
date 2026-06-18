package tutorials4j.framework.feature.schedule.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface JobLogRepository extends BaseRepository<JobLogEntity, Long> {

  @Override
  @EntityGraph(value = "JobLogEntity.withJob")
  Page<JobLogEntity> findAll(Specification<JobLogEntity> spec, Pageable pageable);
}
