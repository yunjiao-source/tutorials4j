package tutorials4j.framework.common.core.exception.feedback;

import cn.hutool.http.HttpStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class UnauthorizedFeedback extends Feedback {

  public UnauthorizedFeedback(String message) {
    super(message, HttpStatus.HTTP_UNAUTHORIZED);
  }
}
