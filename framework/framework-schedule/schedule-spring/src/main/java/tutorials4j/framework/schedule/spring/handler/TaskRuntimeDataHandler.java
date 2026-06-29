package tutorials4j.framework.schedule.spring.handler;

import tutorials4j.framework.schedule.spring.bean.TaskRuntimeData;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface TaskRuntimeDataHandler {
  void handle(TaskRuntimeData data);
}
