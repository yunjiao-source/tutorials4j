package tutorials4j.toolkit.cache;

import tutorials4j.toolkit.core.exception.ErrorCode;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public enum CacheErrorCode implements ErrorCode {
  NAMED_CACHE_NOT_CONFIG("命名缓存未配置"),
  CACHE_MANAGER_NOT_EXIST("缓存管理器不存在");

  private final String message;

  CacheErrorCode(String message) {
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
