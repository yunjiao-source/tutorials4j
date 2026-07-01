package tutorials4j.framework.common.core.exception.feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class BadRequestErrorFeedback extends Feedback {

  public BadRequestErrorFeedback(String message) {
    super(message, 400);
  }
}
