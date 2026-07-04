package tutorials4j.framework.common.core.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import tutorials4j.framework.common.core.bean.Result;
import tutorials4j.framework.common.core.exception.feedback.Feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public class ErrorCodeException extends BaseRuntimeException {

  private final ErrorCode errorCode;
  private final List<Pair<String, Object>> params = new ArrayList<>();
  private final String detail;

  public ErrorCodeException(ErrorCode errorCode) {
    this(errorCode, null, null, null);
  }

  public ErrorCodeException(ErrorCode errorCode, String detail) {
    this(errorCode, detail, null, null);
  }

  public ErrorCodeException(ErrorCode errorCode, String detail, Throwable cause) {
    this(errorCode, detail, cause, null);
  }

  public ErrorCodeException(ErrorCode errorCode, Throwable cause) {
    this(errorCode, null, cause, null);
  }

  public ErrorCodeException(
      ErrorCode errorCode, String detail, Throwable cause, Map<String, Object> paramMap) {
    super(detail, cause);
    this.errorCode = errorCode;
    this.detail = detail;
    if (paramMap != null) {
      paramMap.forEach(
          (k, v) -> {
            this.params.add(new ImmutablePair<>(k, v));
          });
    }
  }

  // 链式添加参数
  public ErrorCodeException param(String key, Object value) {
    this.params.add(new ImmutablePair<>(key, value));
    return this;
  }

  @Override
  public String getMessage() {
    return getFormattedExceptionMessage(super.getMessage());
  }

  public Result<Void> getResult() {

    Result<Void> result = Result.failure(this.getErrorCode());
    result.errorDetail(this.getDetail()).errorParams(this.getParams());

    //    Feedback feedback = this.getErrorCode().getFeedback();
    //    if (feedback.isSystemError() && this.getCause() != null) {
    //      result.errorStackTrace(this.getCause().getStackTrace());
    //    }
    return result;
  }

  private String getFormattedExceptionMessage(final String baseMessage) {
    final StringBuilder buffer = new StringBuilder(256);
    if (baseMessage != null) {
      buffer.append(baseMessage);
    }

    if (!buffer.isEmpty()) {
      buffer.append('\n');
    }

    Feedback feedback = errorCode.getFeedback();
    List<Pair<String, Object>> contextValues = new ArrayList<>();
    contextValues.add(new ImmutablePair<>("CODE", feedback.getCode()));
    contextValues.add(new ImmutablePair<>("HTTP_STATUS", feedback.getHttpStatus()));
    contextValues.addAll(this.params);

    buffer.append("Exception Context:\n");

    int i = 0;
    for (final Pair<String, Object> pair : contextValues) {
      buffer.append("\t[");
      buffer.append(++i);
      buffer.append(':');
      buffer.append(pair.getKey());
      buffer.append("=");
      final Object value = pair.getValue();
      try {
        buffer.append(Objects.toString(value));
      } catch (final Exception e) {
        buffer.append("Exception thrown on toString(): ");
        buffer.append(ExceptionUtils.getStackTrace(e));
      }
      buffer.append("]\n");
    }

    buffer.append("---------------------------------");
    return buffer.toString();
  }
}
