package tutorials4j.toolkit.cache.exception;

import tutorials4j.toolkit.core.exception.BaseRuntimeException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class NamedCacheMissingConfigException extends BaseRuntimeException {

  public NamedCacheMissingConfigException(String name) {
    super("命名缓存缺少配置：" + name);
  }
}
