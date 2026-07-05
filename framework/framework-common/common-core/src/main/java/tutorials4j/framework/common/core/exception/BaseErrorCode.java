package tutorials4j.framework.common.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.feedback.BadRequestErrorFeedback;
import tutorials4j.framework.common.core.exception.feedback.Feedback;
import tutorials4j.framework.common.core.exception.feedback.ForbiddenFeedback;
import tutorials4j.framework.common.core.exception.feedback.InternalServerErrorFeedback;
import tutorials4j.framework.common.core.exception.feedback.MethodNotAllowedFeedback;
import tutorials4j.framework.common.core.exception.feedback.NoContentFeedback;
import tutorials4j.framework.common.core.exception.feedback.NotAcceptableFeedback;
import tutorials4j.framework.common.core.exception.feedback.NotFoundFeedback;
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
  METHOD_NOT_ALLOWED(new MethodNotAllowedFeedback("方法不允许")),
  NOT_ACCEPTABLE(new NotAcceptableFeedback("不接受的请求")),
  PRECONDITION_FAILED(new PreconditionFailedFeedback("客户端发出的请求缺少必要的先决条件")),
  HTTP_MEDIA_TYPE_UNSUPPORTED(new UnsupportedMediaTypeFeedback("服务器无法处理请求中携带的实体内容的格式")),
  INTERNAL_SERVER_ERROR(new InternalServerErrorFeedback("服务器内部错误")),
  NOT_FOUND(new NotFoundFeedback("资源未找到")),
  MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION(new InternalServerErrorFeedback("请求参数缺失")),
  NULL_POINTER_EXCEPTION(new InternalServerErrorFeedback("发生了空指针异常")),
  TYPE_MISMATCH_EXCEPTION(new InternalServerErrorFeedback("类型转换失败")),
  SERVICE_UNAVAILABLE(new ServiceUnavailableFeedback("服务不可用"));

  private final Feedback feedback;

  BaseErrorCode(Feedback feedback) {
    this.feedback = feedback;
    feedback.setCode(this.name());
  }
}
