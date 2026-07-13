package tutorials4j.framework.data.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum DataErrorCode implements ErrorCode {
  DATA_SOURCE_NOT_EXIST("数据源不存在"),
  DATA_SOURCE_NOT_SUPPORT("不支持的数据源"),
  DATA_SOURCE_TYPE_MISMATCH("数据源类型不匹配"),
  DATA_ENTITY_NOT_EXIST("实体不存在"),
  DATA_ENTITY_RESERVED_CANNT_REMOVE("保留数据，不能删除"),
  ;

  private final Feedback feedback;

  DataErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
