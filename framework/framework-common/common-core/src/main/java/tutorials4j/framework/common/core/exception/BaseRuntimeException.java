package tutorials4j.framework.common.core.exception;

/**
 * 基础运行时异常（unchecked exception），作为框架中非受检异常的基类。
 *
 * <p>提供与 {@link RuntimeException} 一致的多种构造方式，业务层可通过继承该类 定义自己的运行时异常。
 *
 * @author Yun Jiao
 */
public class BaseRuntimeException extends RuntimeException {

  /** 无参构造。 */
  public BaseRuntimeException() {}

  /**
   * 带消息的构造。
   *
   * @param message 异常消息
   */
  public BaseRuntimeException(String message) {
    super(message);
  }

  /**
   * 带消息和原因的构造。
   *
   * @param message 异常消息
   * @param cause 原始异常
   */
  public BaseRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * 带原因的构造。
   *
   * @param cause 原始异常
   */
  public BaseRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   * 完整参数的构造。
   *
   * @param message 异常消息
   * @param cause 原始异常
   * @param enableSuppression 是否允许抑制
   * @param writableStackTrace 是否写入堆栈信息
   */
  public BaseRuntimeException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
