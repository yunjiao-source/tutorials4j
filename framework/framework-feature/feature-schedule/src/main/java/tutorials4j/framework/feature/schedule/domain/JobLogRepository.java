package tutorials4j.framework.feature.schedule.domain;

import org.springframework.stereotype.Repository;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Repository
public interface JobLogRepository extends BaseRepository<JobLogEntity, Long> {}
