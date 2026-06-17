package tutorials4j.framework.feature.schedule.domain;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tutorials4j.framework.schedule.core.repository.TaskRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class DatabaseTaskRepository implements TaskRepository<JobEntity> {
  private final JobRepository jobRepository;

  @Override
  public Optional<JobEntity> findByTaskCode(String taskCode) {
    return jobRepository.findByTaskCode(taskCode);
  }

  @Override
  public List<JobEntity> findAll() {
    return jobRepository.findAll();
  }
}
