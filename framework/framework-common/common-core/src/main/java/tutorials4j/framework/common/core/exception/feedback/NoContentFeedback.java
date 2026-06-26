package tutorials4j.framework.common.core.exception.feedback;

import cn.hutool.http.HttpStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class NoContentFeedback extends Feedback {

  public NoContentFeedback(String message) {
    super(message, HttpStatus.HTTP_NO_CONTENT);
  }
}
