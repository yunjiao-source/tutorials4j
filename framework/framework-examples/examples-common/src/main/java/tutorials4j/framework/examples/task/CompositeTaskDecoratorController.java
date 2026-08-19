package tutorials4j.framework.examples.task;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 组合任务装饰器示例控制器。
 *
 * <p>提供触发异步任务执行的接口，用于演示组合任务装饰器的效果。
 *
 * @author Yun Jiao
 */
@RequestMapping("composite-task-decorator")
@RestController
@RequiredArgsConstructor
public class CompositeTaskDecoratorController {
  private final CompositeTaskDecoratorService service;

  /**
   * 触发异步任务执行。
   *
   * @throws InterruptedException 任务执行被中断时抛出
   */
  @GetMapping("/async-exec")
  public void asyncExec() throws InterruptedException {
    service.exec();
  }
}
