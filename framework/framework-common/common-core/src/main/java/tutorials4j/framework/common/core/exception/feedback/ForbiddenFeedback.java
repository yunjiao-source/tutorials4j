package tutorials4j.framework.common.core.exception.feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ForbiddenFeedback extends Feedback {

  public ForbiddenFeedback(String message) {
    super(message, 403);
  }
}
