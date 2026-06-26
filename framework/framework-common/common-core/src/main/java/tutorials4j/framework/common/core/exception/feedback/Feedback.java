package tutorials4j.framework.common.core.exception.feedback;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@RequiredArgsConstructor
public abstract class Feedback {
  private final String message;
  private final int httpStatus;
  private String code;

  public boolean isSystemError() {
    return httpStatus >= 500;
  }
}
