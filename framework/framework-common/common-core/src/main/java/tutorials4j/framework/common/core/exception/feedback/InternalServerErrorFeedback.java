package tutorials4j.framework.common.core.exception.feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class InternalServerErrorFeedback extends Feedback {

  public InternalServerErrorFeedback(String message) {
    super(message, 500);
  }
}
