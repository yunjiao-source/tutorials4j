package tutorials4j.framework.tenant.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.feedback.Feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum TenantErrorCode implements ErrorCode {
  ;

  private final Feedback feedback;

  TenantErrorCode(Feedback feedback) {
    this.feedback = feedback;
    feedback.setCode(this.name());
  }
}
