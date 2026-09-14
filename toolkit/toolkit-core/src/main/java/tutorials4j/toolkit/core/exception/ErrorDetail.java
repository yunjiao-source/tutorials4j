package tutorials4j.toolkit.core.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Accessors(chain = true)
public class ErrorDetail {

  private Instant timestamp;
  private String code;
  private String traceId;
  private String className;
  private Map<String, String> fieldErrors = new LinkedHashMap<>();
  private StackTraceElement[] stackTrace;
  private Map<String, Object> params = new LinkedHashMap<>();

  public void addFieldError(String code, String error) {
    fieldErrors.put(code, error);
  }

  public void addParam(String code, Object value) {
    params.put(code, value);
  }
}
