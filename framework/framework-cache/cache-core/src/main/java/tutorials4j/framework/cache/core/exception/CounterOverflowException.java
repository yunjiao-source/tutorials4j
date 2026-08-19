package tutorials4j.framework.cache.core.exception;

import tutorials4j.framework.common.core.exception.BaseException;

/**
 * 计数器溢出异常。
 *
 * <p>当计数器达到允许的最大次数时抛出，用于提示计数上限已被突破。
 *
 * @author Yun Jiao
 */
public class CounterOverflowException extends BaseException {

  /**
   * 构造计数器溢出异常。
   *
   * @param maxTimes 允许的最大次数
   */
  public CounterOverflowException(int maxTimes) {
    super("计数器溢出，最大值是" + maxTimes);
  }
}
