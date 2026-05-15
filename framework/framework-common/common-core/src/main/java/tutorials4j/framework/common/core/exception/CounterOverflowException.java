package tutorials4j.framework.common.core.exception;

/**
 * 计数器溢出
 *
 * @author Yun Jiao
 */
public class CounterOverflowException extends FrameworkException {

  public CounterOverflowException() {
    super("计数器溢出");
  }

  public CounterOverflowException(int times) {
    this();
    addContextValue("计数器最大值", times);
  }
}
