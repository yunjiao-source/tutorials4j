package tutorials4j.framework.common.core.exception.feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class NotAcceptableFeedback extends Feedback {

  public NotAcceptableFeedback(String message) {
    super(message, 406);
  }
}
