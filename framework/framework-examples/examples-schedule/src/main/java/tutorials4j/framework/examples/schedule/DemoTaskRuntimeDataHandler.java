package tutorials4j.framework.examples.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.spring.bean.TaskRuntimeData;
import tutorials4j.framework.schedule.spring.handler.TaskRuntimeDataHandler;

/**
 * 任务实时运行数据处理器演示：以异步方式打印任务的实时运行状态日志。
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class DemoTaskRuntimeDataHandler implements TaskRuntimeDataHandler {

  /** {@inheritDoc} */
  @Override
  @Async
  public void handle(TaskRuntimeData data) {
    log.info("任务[{}]实时运行状态：{}", data.taskCode(), data.taskStatus());
  }
}
