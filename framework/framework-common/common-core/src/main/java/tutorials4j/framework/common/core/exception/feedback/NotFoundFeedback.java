package tutorials4j.framework.common.core.exception.feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class NotFoundFeedback extends Feedback {

  public NotFoundFeedback(String message) {
    super(message, 404);
  }
}
