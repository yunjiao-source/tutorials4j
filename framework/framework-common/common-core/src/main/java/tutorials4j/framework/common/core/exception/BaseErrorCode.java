package tutorials4j.framework.common.core.exception;

import lombok.Getter;

/**
 * 基础错误码枚举，定义框架与业务通用的错误场景。
 *
 * <p>每个常量携带一个人类可读的错误提示信息，并实现 {@link ErrorCode} 接口， 可通过 {@link #getFeedback()} 获取统一的错误反馈对象。
 *
 * @author Yun Jiao
 */
@Getter
public enum BaseErrorCode implements ErrorCode {
  /** 服务内部异常 */
  INTERNAL_SERVER_ERROR("服务内部异常"),
  /** 检查异常包装 */
  WRAP_CHECK_EXCEPTION("检查异常包装"),
  /** 请求内容在语义或业务逻辑上有错误 */
  UNPROCESSABLE_ENTITY("请求内容在语义或业务逻辑上有错误"),
  /** 服务器拒绝接收客户端发送的请求体格式 */
  UNSUPPORTED_MEDIA_TYPE("服务器拒绝接收客户端发送的请求体格式"),
  /** 服务器无法提供客户端在 Accept 头中指定的响应格式 */
  NOT_ACCEPTABLE("服务器无法提供客户端在 Accept 头中指定的响应格式"),
  /** 服务器识别了请求方法，但目标资源不支持该方法 */
  METHOD_NOT_ALLOWED("服务器识别了请求方法，但目标资源不支持该方法"),
  /** 请求因语法错误或格式无效 */
  BAD_REQUEST("请求因语法错误或格式无效"),
  /** 服务器无法找到所请求的资源 */
  NOT_FOUND("服务器无法找到所请求的资源");

  /** 该错误码对应的错误反馈信息 */
  private final Feedback feedback;

  /**
   * 构造错误码常量。
   *
   * @param message 错误提示信息
   */
  BaseErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
