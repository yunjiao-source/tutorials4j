package tutorials4j.framework.cache.core.lock;

import java.time.Duration;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface Lockable {
  String key();

  Duration waitTime();

  Duration expireTime();

  default void handleException(Exception exception) {}
}
