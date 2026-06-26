package tutorials4j.framework.cache.core.exception;

import tutorials4j.framework.common.core.exception.BaseException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class CounterOverflowException extends BaseException {

  public CounterOverflowException(int maxTimes) {
    super("计数器溢出，最大值是" + maxTimes);
  }
}
