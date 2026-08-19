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
 * 任务日志业务服务。
 *
 * <p>提供任务日志的分页与列表查询能力。
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class JobLogService implements BaseService<JobLogEntity, Long> {
  private final JobLogRepository jobLogRepository;

  /**
   * 返回任务日志数据仓库实例。
   *
   * @return 任务日志数据仓库
   */
  @Override
  public BaseRepository<JobLogEntity, Long> getRepository() {
    return jobLogRepository;
  }

  /**
   * 按条件分页查询任务日志。
   *
   * @param query 查询条件
   * @param pageable 分页参数
   * @return 任务日志分页结果
   */
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public Page<JobLogEntity> find(JobLogQuery query, Pageable pageable) {
    return jobLogRepository.findAll(query.buildSpecification(), pageable);
  }

  /**
   * 按条件查询全部任务日志。
   *
   * @param query 查询条件
   * @return 任务日志列表
   */
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public List<JobLogEntity> find(JobLogQuery query) {
    return this.findAll(query.buildSpecification());
  }
}
