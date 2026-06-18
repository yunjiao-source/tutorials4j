package tutorials4j.framework.schedule.core.component;

import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.schedule.core.bean.TaskRuntimeData;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class LoggingTaskRuntimeDataHandler implements TaskRuntimeDataHandler {

  @Override
  public void handle(TaskRuntimeData data) {
    log.debug("[SCHEDULE-CORE] TaskRuntimeData = {}", data);
  }
}
