package tutorials4j.framework.feature.schedule.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;
import tutorials4j.framework.data.hibernate.domain.BaseService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class JobLogService implements BaseService<JobLogEntity, Long> {
  private final JobLogRepository jobLogRepository;

  @Override
  public BaseRepository<JobLogEntity, Long> getRepository() {
    return jobLogRepository;
  }

  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public Page<JobLogEntity> find(JobLogQuery query, Pageable pageable) {
    return jobLogRepository.findAll(query.buildSpecification(), pageable);
  }

  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public List<JobLogEntity> find(JobLogQuery query) {
    return this.findAll(query.buildSpecification());
  }
}
