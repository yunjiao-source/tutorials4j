package tutorials4j.framework.schedule.spring.repository;

import java.util.List;
import java.util.Optional;
import tutorials4j.framework.schedule.core.bean.Task;

/**
 * 定时任务数据仓库接口。
 *
 * <p>用于按任务编码查询任务以及获取全部任务，是任务调度模块的数据访问抽象。
 *
 * @param <T> 任务类型，需继承 {@link Task}
 * @author Yun Jiao
 */
public interface TaskRepository<T extends Task> {

  /**
   * 根据任务编码查询任务。
   *
   * @param taskCode 任务编码
   * @return 任务的可选结果；不存在时返回 {@link Optional#empty()}
   */
  Optional<T> findByTaskCode(String taskCode);

  /**
   * 获取全部任务。
   *
   * @return 任务列表
   */
  List<T> findAll();
}
