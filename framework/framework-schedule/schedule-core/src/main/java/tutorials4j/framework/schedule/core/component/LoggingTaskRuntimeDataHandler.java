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
    log.warn("需自定义处理的关键数据！！！ {}", data);
  }
}
