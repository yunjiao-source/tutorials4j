package tutorials4j.framework.common.core.exception;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class JsonException extends FrameworkRuntimeException {

  public JsonException(Throwable cause) {
    super("Json处理异常", cause);
  }
}
