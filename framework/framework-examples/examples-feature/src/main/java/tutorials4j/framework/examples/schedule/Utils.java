package tutorials4j.framework.examples.schedule;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class Utils {
  public static void sleep() {
    long milli = ThreadLocalRandom.current().nextInt(1000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
