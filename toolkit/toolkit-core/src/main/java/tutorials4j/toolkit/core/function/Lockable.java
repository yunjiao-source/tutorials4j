package tutorials4j.toolkit.core.function;

import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.toolkit.core.exception.CommonErrorCode;
import tutorials4j.toolkit.core.exception.ErrorCode;
import tutorials4j.toolkit.core.exception.ErrorCodeException;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public interface Lockable {
  default String prefix() {
    return "";
  }
  ;

  String key(Map<String, String> params);

  Duration waitTime();

  Duration expireTime();

  default void handleException(Exception exception) {
    if (exception instanceof ErrorCodeException ece) {
      ErrorCode errorCode = ece.getErrorCode();
      if (CommonErrorCode.ACQUIRE_LOCK_FAIL.equals(errorCode)) {
        return;
      }
    }

    LogHolder.log.error("加锁执行时异常", exception);
  }

  @Slf4j
  final class LogHolder {}
}
