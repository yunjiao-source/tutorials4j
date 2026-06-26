package tutorials4j.framework.common.core.exception.feedback;

import cn.hutool.http.HttpStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class BadMethodFeedback extends Feedback {

  public BadMethodFeedback(String message) {
    super(message, HttpStatus.HTTP_BAD_METHOD);
  }
}
