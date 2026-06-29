package tutorials4j.framework.examples.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.spring.bean.TaskRuntimeData;
import tutorials4j.framework.schedule.spring.handler.TaskRuntimeDataHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class DemoTaskRuntimeDataHandler implements TaskRuntimeDataHandler {

  @Override
  @Async
  public void handle(TaskRuntimeData data) {
    log.info("任务[{}]实时运行状态：{}", data.taskCode(), data.taskStatus());
  }
}
