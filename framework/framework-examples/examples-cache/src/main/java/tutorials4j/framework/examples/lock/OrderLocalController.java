package tutorials4j.framework.examples.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试执行接口
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("/local")
@RequiredArgsConstructor
public class OrderLocalController {
  private final OrderLocalService orderLocalService;

  @GetMapping("non-wait-time")
  public void nonWaitTime(@RequestParam("orderId") String orderId) {
    log.info("> {}", Thread.currentThread().getName());
    orderLocalService.nonWaitTime("1");
  }

  @GetMapping("wait-time")
  public void waitTime(@RequestParam("orderId") String orderId) {
    log.info("> {}", Thread.currentThread().getName());
    orderLocalService.waitTime(orderId);
  }
}
