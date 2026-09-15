package tutorials4j.toolkit.data.exception;

import tutorials4j.toolkit.core.exception.ErrorCode;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public enum DataErrorCode implements ErrorCode {
  FIND_ENTITY_BY_ID_NOT_EXISTS("查询实体不存在"),
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
