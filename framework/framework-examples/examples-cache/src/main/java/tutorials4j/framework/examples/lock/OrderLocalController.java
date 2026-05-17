package tutorials4j.framework.examples.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试执行接口
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/local")
@RequiredArgsConstructor
public class OrderLocalController {
  private final OrderLocalService orderLocalService;

  @GetMapping("non-wait-time")
  public void nonWaitTime() {
    orderLocalService.nonWaitTime("1");
  }

  @GetMapping("wait-time")
  public void waitTime() {
    orderLocalService.waitTime("2");
  }
}
