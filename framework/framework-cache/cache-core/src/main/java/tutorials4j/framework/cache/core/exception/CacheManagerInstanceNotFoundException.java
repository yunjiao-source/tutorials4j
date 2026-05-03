package tutorials4j.framework.cache.core.exception;

/**
 * 未找到缓存管理器实例
 *
 * @author Yun Jiao
 */
public class CacheManagerInstanceNotFoundException extends CacheFrameworkException{
    public CacheManagerInstanceNotFoundException(String message) {
        super("未找到缓存管理器实例:" + message);
    }
}
