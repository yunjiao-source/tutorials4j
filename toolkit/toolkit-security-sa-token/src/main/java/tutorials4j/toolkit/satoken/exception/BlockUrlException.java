package tutorials4j.toolkit.satoken.exception;

import cn.dev33.satoken.exception.SaTokenException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class BlockUrlException extends SaTokenException {
  /** 异常提示语 */
  public static final String BE_MESSAGE = "禁止访问:";

  public BlockUrlException(String url) {
    super(BE_MESSAGE + url);
  }
}
