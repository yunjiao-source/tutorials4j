package tutorials4j.toolkit.cache;

import org.springframework.cache.CacheManager;
import tutorials4j.toolkit.core.function.BeanCreator;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public interface CacheManagerCreator<T extends CacheManager> extends BeanCreator<T> {
  CacheType getCacheType();
}
