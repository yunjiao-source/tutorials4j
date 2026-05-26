package tutorials4j.springboot3.task;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import tutorials4j.springboot3.common.jpa.RemindTask;
import tutorials4j.springboot3.common.jpa.RemindTaskService;

/**
 * 自定义出发任务
 *
 * @author yangyunjiao
 */
@Slf4j
@RequiredArgsConstructor
public class CustomTriggerTask implements Runnable {
  private final RemindTask task;
  private final RemindTaskService remindTaskService;
  private final ApplicationContext applicationContext;

  private boolean initialized = false;
  private Remind remind;

  @SneakyThrows
  @Override
  public void run() {
    if (!initialized) {
      this.init();
    }

    try {
      remind.execute();
    } catch (Exception e) {
      remindTaskService.saveException(task, e);
      log.error("任务异常", e);
    }
  }

  private synchronized void init()
      throws ClassNotFoundException,
          NoSuchMethodException,
          InstantiationException,
          IllegalAccessException {
    if (initialized) {
      return;
    }
    String beanClazz = task.getBeanClazz();
    Class<?> clazz = Class.forName(beanClazz);
    // 从spring容器中获取实例
    remind = (Remind) applicationContext.getBean(clazz);
    initialized = true;
  }
}
