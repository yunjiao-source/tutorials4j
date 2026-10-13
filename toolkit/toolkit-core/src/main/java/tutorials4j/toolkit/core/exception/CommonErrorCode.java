package tutorials4j.toolkit.core.exception;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public enum CommonErrorCode implements ErrorCode {
  SYSTEM_EXCEPTION("系统异常"),
  LOCK_RELEASE_FAIL("释放锁失败"),
  ACQUIRE_LOCK_FAIL("获取锁失败");

  private final String message;

  CommonErrorCode(String message) {
    this.message = message;
  }

  @Override
  public String getCode() {
    return name();
  }

  @Override
  public String getMessage() {
    return message;
  }
}
