package tutorials4j.framework.tenant.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * 租户模块错误码枚举：定义租户模块相关的错误码与反馈信息（目前暂未定义具体错误码）。
 *
 * @author Yun Jiao
 */
@Getter
public enum TenantErrorCode implements ErrorCode {
  ;

  private final Feedback feedback;

  TenantErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
