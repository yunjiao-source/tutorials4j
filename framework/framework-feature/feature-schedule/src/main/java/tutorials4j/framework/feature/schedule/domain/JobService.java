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
public class JobService implements BaseService<JobEntity, Long> {
  private final JobRepository jobRepository;

  @Override
  public BaseRepository<JobEntity, Long> getRepository() {
    return jobRepository;
  }

  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public Page<JobEntity> find(JobQuery query, Pageable pageable) {
    return jobRepository.findAll(query.buildSpecification(), pageable);
  }

  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public List<JobEntity> find(JobQuery query) {
    return this.findAll(query.buildSpecification());
  }
}
