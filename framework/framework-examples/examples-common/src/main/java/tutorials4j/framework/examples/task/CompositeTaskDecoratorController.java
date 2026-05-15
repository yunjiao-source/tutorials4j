package tutorials4j.framework.examples.task;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 *
 * @author Yun Jiao
 */
@RequestMapping("composite-task-decorator")
@RestController
@RequiredArgsConstructor
public class CompositeTaskDecoratorController {
  private final CompositeTaskDecoratorService service;

  @GetMapping("/async-exec")
  public void asyncExec() throws InterruptedException {
    service.exec();
  }
}
