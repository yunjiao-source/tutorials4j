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
 * 任务业务服务。
 *
 * <p>提供任务的分页与列表查询能力。
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class JobService implements BaseService<JobEntity, Long> {
  private final JobRepository jobRepository;

  /**
   * 返回任务数据仓库实例。
   *
   * @return 任务数据仓库
   */
  @Override
  public BaseRepository<JobEntity, Long> getRepository() {
    return jobRepository;
  }

  /**
   * 按条件分页查询任务。
   *
   * @param query 查询条件
   * @param pageable 分页参数
   * @return 任务分页结果
   */
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public Page<JobEntity> find(JobQuery query, Pageable pageable) {
    return jobRepository.findAll(query.buildSpecification(), pageable);
  }

  /**
   * 按条件查询全部任务。
   *
   * @param query 查询条件
   * @return 任务列表
   */
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public List<JobEntity> find(JobQuery query) {
    return this.findAll(query.buildSpecification());
  }
}
