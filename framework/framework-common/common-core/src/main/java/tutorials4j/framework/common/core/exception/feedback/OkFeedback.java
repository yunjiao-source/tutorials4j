package tutorials4j.framework.common.core.exception.feedback;

import cn.hutool.http.HttpStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class OkFeedback extends Feedback {

  public OkFeedback(String message) {
    super(message, HttpStatus.HTTP_OK);
  }
}
