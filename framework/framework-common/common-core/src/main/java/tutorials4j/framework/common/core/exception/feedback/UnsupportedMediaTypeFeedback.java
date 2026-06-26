package tutorials4j.framework.common.core.exception.feedback;

import cn.hutool.http.HttpStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class UnsupportedMediaTypeFeedback extends Feedback {

  public UnsupportedMediaTypeFeedback(String message) {
    super(message, HttpStatus.HTTP_UNSUPPORTED_TYPE);
  }
}
