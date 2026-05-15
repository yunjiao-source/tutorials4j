package tutorials4j.framework.examples.lock.redisson;

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
@RequestMapping("/order-anno")
@RequiredArgsConstructor
public class OrderAnnoController {
  private final OrderAnnoService orderAnnoService;

  @GetMapping("block-auto-renewal")
  public void blockAutoRenewal() {
    orderAnnoService.blockAutoRenewal("1");
  }

  @GetMapping("block-fixed-lease")
  public void blockFixedLease() {
    orderAnnoService.blockFixedLease("2");
  }

  @GetMapping("reentrant-auto-renewal")
  public void reentrantAutoRenewal() {
    orderAnnoService.reentrantAutoRenewal("3");
  }

  @GetMapping("reentrant-fixed-lease")
  public void reentrantFixedLease() {
    orderAnnoService.reentrantFixedLease("4");
  }
}
