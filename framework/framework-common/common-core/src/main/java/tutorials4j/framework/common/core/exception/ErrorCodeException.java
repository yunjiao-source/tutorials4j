package tutorials4j.framework.common.core.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import tutorials4j.framework.common.core.bean.Result;

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

  public Result<Void> getResult() {
    Result<Void> result = Result.failure(errorCode.getFeedback());
    result.errorDetail(this.getDetail()).errorParams(this.getParams());
    return result;
  }
}
