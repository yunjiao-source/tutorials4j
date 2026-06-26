package tutorials4j.framework.common.core.exception.feedback;

import cn.hutool.http.HttpStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class NotFoundFeedback extends Feedback {

  public NotFoundFeedback(String message) {
    super(message, HttpStatus.HTTP_NOT_FOUND);
  }
}
