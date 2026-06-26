package tutorials4j.framework.feature.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.feedback.Feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum FeatureErrorCode implements ErrorCode {
  ;

  private final Feedback feedback;

  FeatureErrorCode(Feedback feedback) {
    this.feedback = feedback;
    feedback.setCode(this.name());
  }
}
