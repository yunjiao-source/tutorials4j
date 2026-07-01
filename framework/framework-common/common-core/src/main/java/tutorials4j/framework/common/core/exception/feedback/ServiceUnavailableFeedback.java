package tutorials4j.framework.common.core.exception.feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ServiceUnavailableFeedback extends Feedback {

  public ServiceUnavailableFeedback(String message) {
    super(message, 503);
  }
}
