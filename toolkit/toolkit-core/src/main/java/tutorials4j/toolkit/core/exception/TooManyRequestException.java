package tutorials4j.toolkit.core.exception;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class TooManyRequestException extends BaseRuntimeException {

  public TooManyRequestException() {
    super("请求过于频繁, 请稍后重试");
  }
}
