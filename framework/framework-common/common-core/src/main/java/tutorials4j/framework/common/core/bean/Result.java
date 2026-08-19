package tutorials4j.framework.common.core.bean;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * 统一 API 响应结果封装。
 *
 * <p>包含状态码（code）、提示信息（message）、业务数据（data）以及可选的错误详情（error）等字段， 提供 success/failure
 * 静态工厂方法与链式设置方法，便于构造统一的接口返回结构。
 *
 * @param <T> 业务数据类型
 * @author Yun Jiao
 */
@Data
public class Result<T> {
  /** 响应创建时间戳。 */
  private final Instant timestamp = Instant.now();

  /** 提示信息（如错误提示或操作结果说明）。 */
  private String message;

  /** 请求路径。 */
  private String path;

  /** 业务数据。 */
  private T data;

  /** 状态码。 */
  private String code;

  /** 链路追踪 ID。 */
  private String traceId;

  /** 错误详情。 */
  private Error error;

  /**
   * 构建一个成功的空响应结果。
   *
   * @return 成功的响应结果（无业务数据）
   */
  public static Result<Void> success() {
    return of(null, null);
  }

  /**
   * 构建一个携带业务数据的成功响应结果。
   *
   * @param data 业务数据
   * @return 成功的响应结果
   */
  public static <T> Result<T> success(T data) {
    return of(null, data);
  }

  /**
   * 根据反馈信息构建一个失败的响应结果。
   *
   * @param feedback 错误反馈信息
   * @return 失败的响应结果
   */
  public static Result<Void> failure(Feedback feedback) {
    return of(feedback, null);
  }

  /**
   * 根据反馈信息与业务数据构建响应结果。
   *
   * @param feedback 错误反馈信息，可为空
   * @param data 业务数据
   * @return 构建完成的响应结果
   */
  private static <T> Result<T> of(Feedback feedback, T data) {
    Result<T> result = new Result<>();
    if (feedback != null) {
      result.code = feedback.code();
      result.message = feedback.message();
    }
    result.data = data;
    return result;
  }

  // 链式设置
  /**
   * 链式设置请求路径。
   *
   * @param path 请求路径
   * @return 当前响应结果
   */
  public Result<T> path(String path) {
    this.path = path;
    return this;
  }

  /**
   * 链式设置链路追踪 ID。
   *
   * @param traceId 链路追踪 ID
   * @return 当前响应结果
   */
  public Result<T> traceId(String traceId) {
    this.traceId = traceId;
    return this;
  }

  /**
   * 链式设置错误详情。
   *
   * @param detail 错误详情
   * @return 当前响应结果
   */
  public Result<T> errorDetail(String detail) {
    if (this.error == null) {
      this.error = new Error();
    }
    this.error.setDetail(detail);
    return this;
  }

  /**
   * 链式设置错误参数列表。
   *
   * @param params 错误参数列表（参数名与值的键值对）
   * @return 当前响应结果
   */
  public Result<T> errorParams(List<Pair<String, Object>> params) {
    if (this.error == null) {
      this.error = new Error();
    }
    this.error.setParams(params);
    return this;
  }

  /**
   * 链式设置错误堆栈信息。
   *
   * @param stackTrace 错误堆栈
   * @return 当前响应结果
   */
  public Result<T> errorStackTrace(StackTraceElement[] stackTrace) {
    if (this.error == null) {
      this.error = new Error();
    }
    this.error.setStackTrace(stackTrace);
    return this;
  }

  /**
   * 链式设置字段级错误信息。
   *
   * @param fieldErrors 字段错误映射（字段名到错误信息的映射）
   * @return 当前响应结果
   */
  public Result<T> fieldErrors(Map<String, String> fieldErrors) {
    if (this.error == null) {
      this.error = new Error();
    }
    this.error.setFieldErrors(fieldErrors);
    return this;
  }

  /** 响应结果中的错误详情信息。 */
  @Data
  public static class Error {
    /** 错误详情描述。 */
    private String detail;

    /** 字段级错误信息映射（字段名到错误信息的映射）。 */
    private Map<String, String> fieldErrors;

    /** 错误堆栈。 */
    private StackTraceElement[] stackTrace;

    /** 错误参数列表。 */
    private List<Pair<String, Object>> params;
  }
}
