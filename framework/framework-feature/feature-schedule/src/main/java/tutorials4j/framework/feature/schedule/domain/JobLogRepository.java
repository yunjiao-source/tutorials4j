package tutorials4j.framework.feature.schedule.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;

/**
 * 任务日志数据仓库接口。
 *
 * <p>继承 {@link BaseRepository}，提供任务日志的持久化访问能力，并通过实体图 预加载关联的任务信息。
 *
 * @author Yun Jiao
 */
@Repository
public interface JobLogRepository extends BaseRepository<JobLogEntity, Long> {

  /**
   * 按条件分页查询任务日志，并预加载关联的任务。
   *
   * @param spec 查询条件
   * @param pageable 分页参数
   * @return 任务日志分页结果
   */
  @Override
  @EntityGraph(value = "JobLogEntity.withJob")
  Page<JobLogEntity> findAll(Specification<JobLogEntity> spec, Pageable pageable);
}
