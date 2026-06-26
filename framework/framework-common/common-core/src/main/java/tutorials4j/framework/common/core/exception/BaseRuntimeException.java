package tutorials4j.framework.common.core.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.feedback.Feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public class BaseRuntimeException extends RuntimeException {
  private final ErrorCode errorCode;
  private final Map<String, Object> params;
  private final String detail;

  public BaseRuntimeException(ErrorCode errorCode) {
    this(errorCode, null, null, null);
  }

  public BaseRuntimeException(ErrorCode errorCode, String detail) {
    this(errorCode, detail, null, null);
  }

  public BaseRuntimeException(ErrorCode errorCode, String detail, Throwable cause) {
    this(errorCode, detail, cause, null);
  }

  public BaseRuntimeException(ErrorCode errorCode, Throwable cause) {
    this(errorCode, null, cause, null);
  }

  public BaseRuntimeException(
      ErrorCode errorCode, String detail, Throwable cause, Map<String, Object> params) {
    super(buildMessage(errorCode, detail), cause);
    this.errorCode = errorCode;
    this.detail = detail;
    this.params = params != null ? params : new HashMap<>();
  }

  private static String buildMessage(ErrorCode errorCode, String detail) {
    Feedback feedback = errorCode.getFeedback();
    String message =
        "[" + feedback.getCode() + " | " + feedback.getHttpStatus() + "]" + feedback.getMessage();
    return detail == null ? message : message + " : " + detail;
  }

  // 链式添加参数
  public BaseRuntimeException param(String key, Object value) {
    this.params.put(key, value);
    return this;
  }

  public Result<Void> getResult() {

    Result<Void> result = Result.failure(this.getErrorCode());
    result.errorDetail(this.getDetail()).errorParams(this.getParams());

    Feedback feedback = this.getErrorCode().getFeedback();
    if (feedback.isSystemError() && this.getCause() != null) {
      result.errorStackTrace(this.getCause().getStackTrace());
    }
    return result;
  }
}
