package tutorials4j.microservice.demo.web.controller;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.microservice.demo.exception.DemoErrorCode;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RestController
@RequestMapping("simple")
public class SimpleController {
  private final AtomicInteger failCount = new AtomicInteger(0);

  @GetMapping("get")
  public String get() {
    log.info(">>> simple 被调用");
    if (ThreadLocalRandom.current().nextInt(100) < 35) {
      throw new RuntimeException("35%机率异常");
    }
    if (ThreadLocalRandom.current().nextInt(100) < 30) {
      throw DemoErrorCode.DEMO_FAIL.throwed("30%机率异常").param("count", failCount.incrementAndGet());
    }
    return "返回请求数据：" + Instant.now().toString();
  }
}
