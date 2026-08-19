package tutorials4j.framework.crypto.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * 加密模块错误码枚举，实现 {@link ErrorCode} 接口，统一描述加密相关的异常信息。
 *
 * @author Yun Jiao
 */
@Getter
public enum CryptoErrorCode implements ErrorCode {
  /** 加密分类不存在。 */
  CRYPTO_CATEGORY_NOT_EXISTS("密码分类不存在"),
  /** 加密处理器不存在。 */
  CRYPTO_PROCESSOR_NOT_EXISTS("密码处理器不存在"),
  /** 摘要分类不存在。 */
  CRYPTO_DIGEST_CATEGORY_NOT_EXISTS("摘要分类不存在"),
  /** 摘要处理器不存在。 */
  CRYPTO_DIGEST_PROCESSOR_NOT_EXISTS("摘要处理器不存在"),
  ;

  /** 错误码对应的反馈信息，包含错误码与描述。 */
  private final Feedback feedback;

  CryptoErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
