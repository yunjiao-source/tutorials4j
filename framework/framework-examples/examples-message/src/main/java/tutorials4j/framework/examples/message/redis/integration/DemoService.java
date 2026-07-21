package tutorials4j.framework.examples.message.redis.integration;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.message.redis.support.bean.RedisMessage;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class DemoService {
  public void handle(DemoData data) {
    int rate = ThreadLocalRandom.current().nextInt(99);
    if (rate < 40) {
      throw new RuntimeException("业务处理异常: " + rate);
    }

    long milli = ThreadLocalRandom.current().nextInt(5000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    log.info("数据处理完成：{}", data.id());
  }

  public void handleError(DemoData data, RedisMessage message) {
    log.error("数据处理异常，转存数据到数据库备份：{}, error={}", data.id(), message.getFailureReasons());
  }
}
