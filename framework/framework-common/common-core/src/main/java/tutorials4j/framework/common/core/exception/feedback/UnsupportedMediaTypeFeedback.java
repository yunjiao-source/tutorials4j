package tutorials4j.framework.common.core.exception.feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class UnsupportedMediaTypeFeedback extends Feedback {

  public UnsupportedMediaTypeFeedback(String message) {
    super(message, 415);
  }
}
