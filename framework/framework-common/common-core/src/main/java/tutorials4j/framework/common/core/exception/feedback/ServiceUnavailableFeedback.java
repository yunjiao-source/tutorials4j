package tutorials4j.framework.common.core.exception.feedback;

import cn.hutool.http.HttpStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ServiceUnavailableFeedback extends Feedback {

  public ServiceUnavailableFeedback(String message) {
    super(message, HttpStatus.HTTP_UNAVAILABLE);
  }
}
