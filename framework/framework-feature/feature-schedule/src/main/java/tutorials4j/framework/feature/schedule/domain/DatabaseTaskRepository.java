package tutorials4j.framework.feature.schedule.domain;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tutorials4j.framework.schedule.spring.repository.TaskRepository;

/**
 * 基于数据库的任务仓库实现。
 *
 * <p>通过 {@link JobRepository} 从数据库加载任务，供调度框架使用。
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class DatabaseTaskRepository implements TaskRepository<JobEntity> {
  private final JobRepository jobRepository;

  /**
   * 按任务编码查询任务。
   *
   * @param taskCode 任务编码
   * @return 任务实体，不存在时返回 {@link Optional#empty()}
   */
  @Override
  public Optional<JobEntity> findByTaskCode(String taskCode) {
    return jobRepository.findByTaskCode(taskCode);
  }

  /**
   * 查询全部任务。
   *
   * @return 任务实体列表
   */
  @Override
  public List<JobEntity> findAll() {
    return jobRepository.findAll();
  }
}
