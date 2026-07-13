package tutorials4j.framework.examples.exception;

import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;
import tutorials4j.framework.common.spring.web.BaseExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum CustomErrorCode implements ErrorCode {
  CUSTOM_EXCEPTION("自定义异常");

  private final Feedback feedback;

  CustomErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }

  static {
    Map<ErrorCode, HttpStatus> map =
        Map.of(CustomErrorCode.CUSTOM_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR);
    BaseExceptionHandler.registeErrorCode(map);
  }
}
