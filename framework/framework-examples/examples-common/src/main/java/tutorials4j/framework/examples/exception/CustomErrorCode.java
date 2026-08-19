package tutorials4j.framework.examples.exception;

import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;
import tutorials4j.framework.common.spring.web.BaseExceptionHandler;

/**
 * 自定义错误码枚举示例。
 *
 * <p>演示如何通过实现 {@link ErrorCode} 接口自定义业务错误码：每个枚举常量持有对应的 {@link Feedback} 反馈信息，并在静态初始化块中将错误码与 HTTP
 * 状态码的映射关系注册到 {@link BaseExceptionHandler}。
 *
 * @author Yun Jiao
 */
@Getter
public enum CustomErrorCode implements ErrorCode {
  /** 自定义异常错误码，对应 HTTP 500 内部服务器错误。 */
  CUSTOM_EXCEPTION("自定义异常");

  /** 错误码对应的反馈信息（包含错误码与提示消息）。 */
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
