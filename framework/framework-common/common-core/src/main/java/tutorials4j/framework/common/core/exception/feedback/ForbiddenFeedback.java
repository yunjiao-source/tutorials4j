package tutorials4j.framework.common.core.exception.feedback;

import cn.hutool.http.HttpStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ForbiddenFeedback extends Feedback {

  public ForbiddenFeedback(String message) {
    super(message, HttpStatus.HTTP_FORBIDDEN);
  }
}
