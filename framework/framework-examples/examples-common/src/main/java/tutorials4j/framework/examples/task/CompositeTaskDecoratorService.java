package tutorials4j.framework.examples.task;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 组合任务装饰器示例服务。
 *
 * <p>通过 {@link Async} 异步方法模拟耗时任务的执行，用于演示组合任务装饰器。
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class CompositeTaskDecoratorService {

  /**
   * 异步执行模拟任务，休眠 3 秒。
   *
   * @throws InterruptedException 任务休眠被中断时抛出
   */
  @Async
  public void exec() throws InterruptedException {
    log.info("async exec");
    TimeUnit.SECONDS.sleep(3);
  }
}
