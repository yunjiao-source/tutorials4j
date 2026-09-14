package tutorials4j.toolkit.core.exception;

/**
 * @author Yun Jiao
 */
public interface ErrorCode {
  String getCode();

  String getMessage();

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
