package tutorials4j.framework.common.core.exception.feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class NoContentFeedback extends Feedback {

  public NoContentFeedback(String message) {
    super(message, 204);
  }
}
