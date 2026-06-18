package tutorials4j.framework.schedule.core.component;

import tutorials4j.framework.schedule.core.bean.TaskRuntimeData;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface TaskRuntimeDataHandler {
  void handle(TaskRuntimeData data);
}
