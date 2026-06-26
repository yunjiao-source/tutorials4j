package tutorials4j.framework.common.core.exception.feedback;

import cn.hutool.http.HttpStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class NotImplementedFeedback extends Feedback {

  public NotImplementedFeedback(String message) {
    super(message, HttpStatus.HTTP_NOT_IMPLEMENTED);
  }
}
