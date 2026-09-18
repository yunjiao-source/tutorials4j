package tutorials4j.toolkit.core.exception;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class UnauthorizedException extends BaseRuntimeException {

  public UnauthorizedException() {
    super("未登录系统");
  }
}
