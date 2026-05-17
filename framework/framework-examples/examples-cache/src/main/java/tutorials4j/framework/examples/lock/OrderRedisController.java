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
@RequestMapping("/redis")
@RequiredArgsConstructor
public class OrderRedisController {
  private final OrderRedisService orderRedisService;

  @GetMapping("non-expire-time")
  public void nonExpireTime() {
    orderRedisService.nonExpireTime("1");
  }

  @GetMapping("expire-time")
  public void expireTime() {
    orderRedisService.expireTime("2");
  }
}
