package tutorials4j.framework.common.core.exception.feedback;

import cn.hutool.http.HttpStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class NotAcceptableFeedback extends Feedback {

  public NotAcceptableFeedback(String message) {
    super(message, HttpStatus.HTTP_NOT_ACCEPTABLE);
  }
}
