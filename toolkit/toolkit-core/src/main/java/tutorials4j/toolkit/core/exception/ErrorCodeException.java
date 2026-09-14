package tutorials4j.toolkit.core.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;

/**
 * 携带错误码的运行时异常。
 *
 * <p>在 {@link BaseRuntimeException} 基础上关联一个 {@link ErrorCode}，并支持携带 详情信息（detail）和键值对参数（params），可通过
 * {@link #getErrorDetail()} 快速转换为 统一的失败结果对象。
 *
 * @author Yun Jiao
 */
@Getter
public class ErrorCodeException extends BaseRuntimeException {

  /** 关联的错误码 */
  private final ErrorCode errorCode;

  /** 附加的错误参数列表（键值对） */
  private final Map<String, Object> params = new LinkedHashMap<>();

  /** 错误详情信息 */
  private final String detail;

  /**
   * 仅携带错误码的构造。
   *
   * @param errorCode 错误码
   */
  public ErrorCodeException(ErrorCode errorCode) {
    this(errorCode, null, null, null);
  }

  /**
   * 携带错误码和详情信息的构造。
   *
   * @param errorCode 错误码
   * @param detail 错误详情信息
   */
  public ErrorCodeException(ErrorCode errorCode, String detail) {
    this(errorCode, detail, null, null);
  }

  /**
   * 携带错误码、详情信息和原因的构造。
   *
   * @param errorCode 错误码
   * @param detail 错误详情信息
   * @param cause 原始异常
   */
  public ErrorCodeException(ErrorCode errorCode, String detail, Throwable cause) {
    this(errorCode, detail, cause, null);
  }

  /**
   * 携带错误码和原因的构造。
   *
   * @param errorCode 错误码
   * @param cause 原始异常
   */
  public ErrorCodeException(ErrorCode errorCode, Throwable cause) {
    this(errorCode, null, cause, null);
  }

  /**
   * 完整参数的构造，可将参数映射转换为参数列表。
   *
   * @param errorCode 错误码
   * @param detail 错误详情信息
   * @param cause 原始异常
   * @param paramMap 附加参数映射（可为 null）
   */
  public ErrorCodeException(
      ErrorCode errorCode, String detail, Throwable cause, Map<String, Object> paramMap) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
    this.detail = detail;
    if (paramMap != null) {
      this.params.putAll(paramMap);
    }
  }

  /**
   * 链式添加一个错误参数并返回当前异常实例。
   *
   * @param key 参数键
   * @param value 参数值
   * @return 当前异常实例，便于继续链式调用
   */
  // 链式添加参数
  public ErrorCodeException param(String key, Object value) {
    this.params.put(key, value);
    return this;
  }

  public ErrorDetail getErrorDetail() {
    return new ErrorDetail()
        .setTimestamp(Instant.now())
        .setFieldErrors(new LinkedHashMap<>())
        .setParams(getParams())
        .setClassName(this.getClass().getName())
        .setCode(getErrorCode().getCode());
  }
}
