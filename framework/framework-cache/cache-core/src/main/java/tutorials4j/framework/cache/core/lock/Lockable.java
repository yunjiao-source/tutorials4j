package tutorials4j.framework.cache.core.lock;

import java.time.Duration;
import tutorials4j.framework.common.core.exception.BaseRuntimeException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface Lockable {
  String key();

  Duration waitTime();

  Duration expireTime();

  default void handleException(BaseRuntimeException exception) {}
}
