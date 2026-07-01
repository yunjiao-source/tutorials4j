package tutorials4j.framework.common.core.exception.feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class MethodNotAllowedFeedback extends Feedback {

  public MethodNotAllowedFeedback(String message) {
    super(message, 405);
  }
}
