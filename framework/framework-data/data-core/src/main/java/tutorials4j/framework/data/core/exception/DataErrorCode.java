package tutorials4j.framework.data.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.feedback.Feedback;
import tutorials4j.framework.common.core.exception.feedback.InternalServerErrorFeedback;
import tutorials4j.framework.common.core.exception.feedback.NotAcceptableFeedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum DataErrorCode implements ErrorCode {
  DATA_SOURCE_NOT_EXIST(new InternalServerErrorFeedback("数据源不存在")),
  DATA_SOURCE_NOT_SUPPORT(new InternalServerErrorFeedback("不支持的数据源")),
  DATA_SOURCE_TYPE_MISMATCH(new InternalServerErrorFeedback("数据源类型不匹配")),
  DATA_ENTITY_NOT_EXIST(new NotAcceptableFeedback("实体不存在")),
  DATA_ENTITY_RESERVED_CANNT_REMOVE(new NotAcceptableFeedback("保留数据，不能删除")),
  ;

  private final Feedback feedback;

  DataErrorCode(Feedback feedback) {
    this.feedback = feedback;
    feedback.setCode(this.name());
  }
}
