package tutorials4j.framework.common.core.exception;

/**
 * 错误码接口，统一描述错误反馈与抛出异常的能力。
 *
 * <p>实现该接口的类型（通常是枚举）需提供 {@link #getFeedback()} 返回错误反馈对象， 并可通过 {@code throwed} 系列默认方法便捷地抛出 {@link
 * ErrorCodeException}。
 *
 * @author Yun Jiao
 */
public interface ErrorCode {

  /**
   * 获取错误码对应的错误反馈对象。
   *
   * @return 错误反馈对象
   */
  Feedback getFeedback();

  /**
   * 抛出携带当前错误码的异常。
   *
   * @return 携带当前错误码的异常实例
   */
  default ErrorCodeException throwed() {
    return new ErrorCodeException(this);
  }

  /**
   * 抛出携带当前错误码和详情信息的异常。
   *
   * @param message 错误详情信息
   * @return 携带当前错误码和详情信息的异常实例
   */
  default ErrorCodeException throwed(String message) {
    return new ErrorCodeException(this, message);
  }

  /**
   * 抛出携带当前错误码、详情信息和原因的异常。
   *
   * @param message 错误详情信息
   * @param cause 原始异常
   * @return 携带错误码、详情和原因的异常实例
   */
  default ErrorCodeException throwed(String message, Throwable cause) {
    return new ErrorCodeException(this, message, cause);
  }

  /**
   * 抛出携带当前错误码和原因的异常。
   *
   * @param cause 原始异常
   * @return 携带错误码和原因的异常实例
   */
  default ErrorCodeException throwed(Throwable cause) {
    return new ErrorCodeException(this, cause);
  }
}
