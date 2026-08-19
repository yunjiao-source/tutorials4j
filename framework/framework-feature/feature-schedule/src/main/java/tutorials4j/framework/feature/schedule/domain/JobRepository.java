package tutorials4j.framework.feature.schedule.domain;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import tutorials4j.framework.data.hibernate.domain.BaseRepository;

/**
 * 任务数据仓库接口。
 *
 * <p>继承 {@link BaseRepository}，提供任务实体的持久化访问能力。
 *
 * @author Yun Jiao
 */
@Repository
public interface JobRepository extends BaseRepository<JobEntity, Long> {

  /**
   * 按任务编码查询任务。
   *
   * @param taskCode 任务编码
   * @return 任务实体，不存在时返回 {@link Optional#empty()}
   */
  Optional<JobEntity> findByTaskCode(String taskCode);
}
