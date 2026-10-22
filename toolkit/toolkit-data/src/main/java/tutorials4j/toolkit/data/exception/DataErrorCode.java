package tutorials4j.toolkit.data.exception;

import tutorials4j.toolkit.core.exception.ErrorCode;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public enum DataErrorCode implements ErrorCode {
  RESERVED_DATA_CAN_NOT_REMOVE("保留数据，不能删除"),
  FIND_ENTITY_NOT_EXISTS("查询实体不存在"),
  ;

  private final String message;

  DataErrorCode(String message) {
    this.message = message;
  }

  @Override
  public String getCode() {
    return name();
  }

  @Override
  public String getMessage() {
    return message;
  }
}
