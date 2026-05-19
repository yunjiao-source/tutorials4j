package tutorials4j.framework.cache.core.exception;

import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * 未找到缓存管理器实例
 *
 * @author Yun Jiao
 */
public class CacheManagerCreatorNotFoundException extends FrameworkRuntimeException {
  public CacheManagerCreatorNotFoundException(String message) {
    super("未找到缓存管理器实例:" + message);
  }
}
