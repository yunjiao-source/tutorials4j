package tutorials4j.springboot3;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;
import tutorials4j.springboot3.qps.MethodQps;

/**
 * 示例服务
 *
 * @author Yun Jiao
 */
@Service
public class DemoService {

  @MethodQps
  public void qps1() {
    try {
      TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(999));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @MethodQps
  public void qps2() {
    try {
      TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(999));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
