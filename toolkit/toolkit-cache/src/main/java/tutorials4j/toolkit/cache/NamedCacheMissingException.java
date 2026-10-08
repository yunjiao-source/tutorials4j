package tutorials4j.toolkit.cache;

import tutorials4j.toolkit.core.exception.BaseRuntimeException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class NamedCacheMissingException extends BaseRuntimeException {

  public NamedCacheMissingException(String cacheName) {
    super("命名缓存未配置：" + cacheName);
  }
}
