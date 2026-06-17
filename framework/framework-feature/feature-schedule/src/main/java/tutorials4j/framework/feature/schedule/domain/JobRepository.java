package tutorials4j.framework.feature.schedule.domain;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface JobRepository extends BaseRepository<JobEntity, Long> {

  Optional<JobEntity> findByTaskCode(String taskCode);
}
