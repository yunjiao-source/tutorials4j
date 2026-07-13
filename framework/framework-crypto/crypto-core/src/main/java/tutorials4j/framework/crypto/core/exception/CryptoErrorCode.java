package tutorials4j.framework.crypto.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum CryptoErrorCode implements ErrorCode {
  CRYPTO_CATEGORY_NOT_EXISTS("密码分类不存在"),
  CRYPTO_PROCESSOR_NOT_EXISTS("密码处理器不存在"),
  CRYPTO_DIGEST_CATEGORY_NOT_EXISTS("摘要分类不存在"),
  CRYPTO_DIGEST_PROCESSOR_NOT_EXISTS("摘要处理器不存在"),
  ;

  private final Feedback feedback;

  CryptoErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
