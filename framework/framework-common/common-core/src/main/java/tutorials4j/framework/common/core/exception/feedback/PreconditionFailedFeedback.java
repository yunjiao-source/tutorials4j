package tutorials4j.framework.common.core.exception.feedback;

import cn.hutool.http.HttpStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class PreconditionFailedFeedback extends Feedback {

  public PreconditionFailedFeedback(String message) {
    super(message, HttpStatus.HTTP_PRECONDITION_REQUIRED);
  }
}
