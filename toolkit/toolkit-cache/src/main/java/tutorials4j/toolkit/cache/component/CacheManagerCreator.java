package tutorials4j.toolkit.cache.component;

import org.springframework.cache.CacheManager;
import tutorials4j.toolkit.cache.CacheType;
import tutorials4j.toolkit.core.function.BeanCreator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface CacheManagerCreator<T extends CacheManager> extends BeanCreator<T> {
  CacheType getCacheType();
}
