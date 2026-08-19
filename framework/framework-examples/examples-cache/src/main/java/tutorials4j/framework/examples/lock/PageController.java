package tutorials4j.framework.examples.lock;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 锁示例页面视图控制器，用于展示锁测试界面。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  /**
   * 跳转到 Redisson 锁测试页面。
   *
   * @return Redisson 锁测试页面视图名称
   */
  @GetMapping("/redisson")
  public String redisson() {
    return "lock/redisson";
  }

  /**
   * 跳转到 Redis 锁测试页面。
   *
   * @return Redis 锁测试页面视图名称
   */
  @GetMapping("/redis")
  public String redis() {
    return "lock/redis";
  }

  /**
   * 跳转到本地锁测试页面。
   *
   * @return 本地锁测试页面视图名称
   */
  @GetMapping("/local")
  public String local() {
    return "lock/local";
  }
}
