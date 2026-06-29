package tutorials4j.framework.schedule.spring.repository;

import java.util.List;
import java.util.Optional;
import tutorials4j.framework.schedule.core.bean.Task;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface TaskRepository<T extends Task> {

  Optional<T> findByTaskCode(String taskCode);

  List<T> findAll();
}
