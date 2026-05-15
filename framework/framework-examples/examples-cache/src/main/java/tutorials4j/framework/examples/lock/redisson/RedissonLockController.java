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
@RequestMapping("/redisson-lock")
@RequiredArgsConstructor
public class RedissonLockController {
    private final OrderService orderService;

    @GetMapping("block-auto-renewal")
    public void blockAutoRenewal() {
        orderService.blockAutoRenewal("1");
    }

    @GetMapping("block-fixed-lease")
    public void blockFixedLease() {
        orderService.blockFixedLease("2");
    }

    @GetMapping("reentrant-auto-renewal")
    public void reentrantAutoRenewal() {
        orderService.reentrantAutoRenewal("3");
    }

    @GetMapping("reentrant-fixed-lease")
    public void reentrantFixedLease() {
        orderService.reentrantFixedLease("4");
    }
}
