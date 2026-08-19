package tutorials4j.framework.data.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * Data 模块错误码枚举。
 *
 * <p>实现 {@link ErrorCode} 接口，集中定义数据源、数据实体等相关的错误码及对应的 反馈信息，供数据访问模块及其上层统一使用。
 *
 * @author Yun Jiao
 */
@Getter
public enum DataErrorCode implements ErrorCode {
  /** 数据源不存在。 */
  DATA_SOURCE_NOT_EXIST("数据源不存在"),
  /** 不支持的数据源。 */
  DATA_SOURCE_NOT_SUPPORT("不支持的数据源"),
  /** 数据源类型不匹配。 */
  DATA_SOURCE_TYPE_MISMATCH("数据源类型不匹配"),
  /** 实体不存在。 */
  DATA_ENTITY_NOT_EXIST("实体不存在"),
  /** 保留数据，不能删除。 */
  DATA_ENTITY_RESERVED_CANNT_REMOVE("保留数据，不能删除"),
  ;

  /** 错误码对应的反馈信息。 */
  private final Feedback feedback;

  /**
   * 以指定提示信息构造错误码。
   *
   * @param message 错误提示信息，将封装进 {@link Feedback}
   */
  DataErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
