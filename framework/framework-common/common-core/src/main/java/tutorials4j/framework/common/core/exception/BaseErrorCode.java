package tutorials4j.framework.common.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.feedback.BadMethodFeedback;
import tutorials4j.framework.common.core.exception.feedback.BadRequestErrorFeedback;
import tutorials4j.framework.common.core.exception.feedback.Feedback;
import tutorials4j.framework.common.core.exception.feedback.ForbiddenFeedback;
import tutorials4j.framework.common.core.exception.feedback.InternalServerErrorFeedback;
import tutorials4j.framework.common.core.exception.feedback.NoContentFeedback;
import tutorials4j.framework.common.core.exception.feedback.NotAcceptableFeedback;
import tutorials4j.framework.common.core.exception.feedback.NotFoundFeedback;
import tutorials4j.framework.common.core.exception.feedback.NotImplementedFeedback;
import tutorials4j.framework.common.core.exception.feedback.OkFeedback;
import tutorials4j.framework.common.core.exception.feedback.PreconditionFailedFeedback;
import tutorials4j.framework.common.core.exception.feedback.ServiceUnavailableFeedback;
import tutorials4j.framework.common.core.exception.feedback.UnauthorizedFeedback;
import tutorials4j.framework.common.core.exception.feedback.UnsupportedMediaTypeFeedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum BaseErrorCode implements ErrorCode {
  OK(new OkFeedback("成功")),
  NO_CONTENT(new NoContentFeedback("无内容")),
  VALIDATION_FAILED(new BadRequestErrorFeedback("接口参数校验失败")),
  UNAUTHORIZED(new UnauthorizedFeedback("未经授权")),
  FORBIDDEN(new ForbiddenFeedback("禁止的请求")),
  METHOD_NOT_ALLOWED(new BadMethodFeedback("方法不允许")),
  NOT_ACCEPTABLE(new NotAcceptableFeedback("不接受的请求")),
  PRECONDITION_FAILED(new PreconditionFailedFeedback("用户端请求信息的先决条件错误")),
  HTTP_MEDIA_TYPE_NOT_ACCEPTABLE(new UnsupportedMediaTypeFeedback("不支持的 Media Type")),
  INTERNAL_SERVER_ERROR(new InternalServerErrorFeedback("服务器内部错误，无法完成请求")),
  SERVER_ERROR(new InternalServerErrorFeedback("服务器遇到意外情况，无法满足请求")),
  ILLEGAL_ARGUMENT_EXCEPTION(new InternalServerErrorFeedback("参数不合法错误，请仔细确认参数使用是否正确")),
  IO_EXCEPTION(new InternalServerErrorFeedback("IO异常")),
  STATIC_RESOURCE_NOT_FOUND(new NotFoundFeedback("静态资源未找到")),
  MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION(
      new InternalServerErrorFeedback("接口参数使用错误或必要参数缺失，请查阅接口文档")),
  NULL_POINTER_EXCEPTION(new InternalServerErrorFeedback("后台代码执行过程中出现了空值")),
  TYPE_MISMATCH_EXCEPTION(new InternalServerErrorFeedback("类型不匹配")),
  THIRD_PARTY_EXCEPTION(new InternalServerErrorFeedback("第三方工具检查异常")),
  NOT_IMPLEMENTED(new NotImplementedFeedback("服务器不支持请求的功能，无法完成请求")),
  SERVICE_UNAVAILABLE(new ServiceUnavailableFeedback("服务不可用"));

  private final Feedback feedback;

  BaseErrorCode(Feedback feedback) {
    this.feedback = feedback;
    feedback.setCode(this.name());
  }
}
