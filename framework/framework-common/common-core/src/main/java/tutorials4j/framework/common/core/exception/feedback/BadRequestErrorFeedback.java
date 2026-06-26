package tutorials4j.framework.common.core.exception.feedback;

import cn.hutool.http.HttpStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class BadRequestErrorFeedback extends Feedback {

  public BadRequestErrorFeedback(String message) {
    super(message, HttpStatus.HTTP_BAD_REQUEST);
  }
}
