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
@RequestMapping("/redisson")
@RequiredArgsConstructor
public class OrderRedissonController {
  private final OrderRedissonService orderRedissonService;

  @GetMapping("block-non-expire-time")
  public void blockNonExpireTime() {
    orderRedissonService.blockNonExpireTime("1");
  }

  @GetMapping("block-expire-time")
  public void blockExpireTime() {
    orderRedissonService.blockExpireTime("2");
  }

  @GetMapping("reentrant-non-expire-time")
  public void reentrantNonExpireTime() {
    orderRedissonService.reentrantNonExpireTime("3");
  }

  @GetMapping("reentrant-expire-time")
  public void reentrantExpireTime() {
    orderRedissonService.reentrantExpireTime("4");
  }
}
