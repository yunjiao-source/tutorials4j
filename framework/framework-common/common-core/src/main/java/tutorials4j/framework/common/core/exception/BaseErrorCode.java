package tutorials4j.framework.common.core.exception;

import lombok.Getter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum BaseErrorCode implements ErrorCode {
  INTERNAL_SERVER_ERROR("服务内部异常"),
  WRAP_CHECK_EXCEPTION("检查异常包装"),
  UNPROCESSABLE_ENTITY("请求内容在语义或业务逻辑上有错误"),
  UNSUPPORTED_MEDIA_TYPE("服务器拒绝接收客户端发送的请求体格式"),
  NOT_ACCEPTABLE("服务器无法提供客户端在 Accept 头中指定的响应格式"),
  METHOD_NOT_ALLOWED("服务器识别了请求方法，但目标资源不支持该方法"),
  BAD_REQUEST("请求因语法错误或格式无效"),
  NOT_FOUND("服务器无法找到所请求的资源");

  private final Feedback feedback;

  BaseErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
