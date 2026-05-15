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
@RequestMapping("/order-call")
@RequiredArgsConstructor
public class OrderCallController {
    private final OrderCallService orderCallService;

    @GetMapping("block-auto-renewal")
    public void blockAutoRenewal() {
        orderCallService.blockAutoRenewal("1");
    }

    @GetMapping("block-fixed-lease")
    public void blockFixedLease() {
        orderCallService.blockFixedLease("2");
    }

    @GetMapping("reentrant-auto-renewal")
    public void reentrantAutoRenewal() {
        orderCallService.reentrantAutoRenewal("3");
    }

    @GetMapping("reentrant-fixed-lease")
    public void reentrantFixedLease() {
        orderCallService.reentrantFixedLease("4");
    }
}
