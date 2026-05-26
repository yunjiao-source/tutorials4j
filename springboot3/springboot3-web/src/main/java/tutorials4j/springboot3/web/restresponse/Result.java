package tutorials4j.springboot3.web.restresponse;

import java.io.Serializable;
import lombok.Data;

/**
 * 统一返回封装类
 *
 * @author Yun Jiao
 */
@Data
public class Result<T> implements Serializable {
  // 状态码（对应ResultCode枚举）
  private Integer code;
  // 提示信息
  private String message;
  // 业务数据（可选，成功时返回，失败时可null）
  private T data;

  // 私有构造方法，禁止外部直接实例化
  protected Result() {}

  // 私有构造方法，用于统一赋值
  protected Result(Integer code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  // ------------------- 静态工厂方法（成功响应）-------------------
  /** 成功响应（无业务数据） */
  public static <T> Result<T> success() {
    return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
  }

  /**
   * 成功响应（带业务数据）
   *
   * @param data 业务数据
   */
  public static <T> Result<T> success(T data) {
    return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
  }

  /**
   * 成功响应（自定义提示信息+业务数据）
   *
   * @param message 自定义提示信息
   * @param data 业务数据
   */
  public static <T> Result<T> success(String message, T data) {
    return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
  }

  // ------------------- 静态工厂方法（失败响应）-------------------
  /**
   * 失败响应（使用预定义的状态码）
   *
   * @param resultCode 状态码枚举
   */
  public static <T> Result<T> fail(ResultCode resultCode) {
    return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
  }

  /**
   * 失败响应（预定义状态码+自定义提示信息）
   *
   * @param resultCode 状态码枚举
   * @param message 自定义提示信息
   */
  public static <T> Result<T> fail(ResultCode resultCode, String message) {
    return new Result<>(resultCode.getCode(), message, null);
  }

  /**
   * 失败响应（自定义状态码+提示信息，无业务数据）
   *
   * @param code 自定义状态码
   * @param message 提示信息
   */
  public static <T> Result<T> fail(Integer code, String message) {
    return new Result<>(code, message, null);
  }

  // ------------------- 静态工厂方法（异常响应）-------------------
  /** 异常响应（默认服务器内部异常） */
  public static <T> Result<T> error() {
    return new Result<>(
        ResultCode.INTERNAL_SERVER_ERROR.getCode(),
        ResultCode.INTERNAL_SERVER_ERROR.getMessage(),
        null);
  }

  /**
   * 异常响应（自定义异常提示信息）
   *
   * @param message 异常提示信息
   */
  public static <T> Result<T> error(String message) {
    return new Result<>(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message, null);
  }
}
