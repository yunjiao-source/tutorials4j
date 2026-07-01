package tutorials4j.framework.common.core.bean;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.feedback.Feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class Result<T> {
  private final Instant timestamp = Instant.now();
  private String message;
  private String path;
  private T data;
  private int status;
  private String code;
  private String traceId;
  private Error error;

  public static Result<Void> noContent() {
    return of(BaseErrorCode.NO_CONTENT, null);
  }

  public static <T> Result<T> success() {
    return of(BaseErrorCode.OK, null);
  }

  public static <T> Result<T> success(T data) {
    return of(BaseErrorCode.OK, data);
  }

  public static <T> Result<T> failure() {
    return of(BaseErrorCode.INTERNAL_SERVER_ERROR, null);
  }

  public static <T> Result<T> failure(ErrorCode errorCode) {
    return of(errorCode, null);
  }

  private static <T> Result<T> of(ErrorCode errorCode, T data) {
    Feedback feedback = errorCode.getFeedback();
    Result<T> result = new Result<>();
    result.code = feedback.getCode();
    result.message = feedback.getMessage();
    result.status = feedback.getHttpStatus();
    result.data = data;
    return result;
  }

  // 链式设置
  public Result<T> path(String path) {
    this.path = path;
    return this;
  }

  public Result<T> traceId(String traceId) {
    this.traceId = traceId;
    return this;
  }

  public Result<T> errorDetail(String detail) {
    if (this.error == null) {
      this.error = new Error();
    }
    this.error.setDetail(detail);
    return this;
  }

  public Result<T> errorParams(List<Pair<String, Object>> params) {
    if (this.error == null) {
      this.error = new Error();
    }
    this.error.setParams(params);
    return this;
  }

  public Result<T> errorStackTrace(StackTraceElement[] stackTrace) {
    if (this.error == null) {
      this.error = new Error();
    }
    this.error.setStackTrace(stackTrace);
    return this;
  }

  public Result<T> fieldErrors(Map<String, String> fieldErrors) {
    if (this.error == null) {
      this.error = new Error();
    }
    this.error.setFieldErrors(fieldErrors);
    return this;
  }

  @Data
  public static class Error {
    private String detail;
    private Map<String, String> fieldErrors;
    private StackTraceElement[] stackTrace;
    private List<Pair<String, Object>> params;
  }
}
