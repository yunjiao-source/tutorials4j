package tutorials4j.framework.common.core.exception.feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class UnauthorizedFeedback extends Feedback {

  public UnauthorizedFeedback(String message) {
    super(message, 401);
  }
}
