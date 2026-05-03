package tutorials4j.framework.cache.core.support;

import org.springframework.cache.CacheManager;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface CacheManagerCreator<T extends CacheManager> {
    T getInstance();

    T newInstance();

    Class<T> getCacheManagerClass();
}
