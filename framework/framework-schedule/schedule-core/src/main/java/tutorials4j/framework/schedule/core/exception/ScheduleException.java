package tutorials4j.framework.schedule.core.exception;

import org.apache.commons.lang3.exception.ExceptionContext;
import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ScheduleException extends FrameworkRuntimeException {

  public ScheduleException() {}

  public ScheduleException(String message) {
    super(message);
  }

  public ScheduleException(String message, Throwable cause) {
    super(message, cause);
  }

  public ScheduleException(String message, Throwable cause, ExceptionContext context) {
    super(message, cause, context);
  }

  public ScheduleException(Throwable cause) {
    super(cause);
  }
}
