package tutorials4j.framework.common.core.exception.feedback;

import cn.hutool.http.HttpStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class InternalServerErrorFeedback extends Feedback {

  public InternalServerErrorFeedback(String message) {
    super(message, HttpStatus.HTTP_INTERNAL_ERROR);
  }
}
