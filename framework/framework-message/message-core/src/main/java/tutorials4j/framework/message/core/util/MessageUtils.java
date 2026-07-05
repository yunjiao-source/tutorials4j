package tutorials4j.framework.message.core.util;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface MessageUtils {
  static void sleepForWait(Duration waitTime) {
    try {
      TimeUnit.MILLISECONDS.sleep(waitTime.toMillis());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
  }
}
