package tutorials4j.framework.cache.core.support;

import org.springframework.cache.CacheManager;
import tutorials4j.framework.common.core.support.BeanCreator;

/**
 * Spring {@link CacheManager} 创建器的泛型接口。
 * <p>
 * 该接口继承自 {@link BeanCreator}，专门用于创建或获取 {@link CacheManager} 及其子类的实例。
 * 具体缓存管理器（如 Redis、Caffeine、多级缓存）的创建器应实现此接口。
 * </p>
 *
 * @param <T> 缓存管理器类型，必须继承自 {@link CacheManager}
 * @author Yun Jiao
 */
public interface CacheManagerCreator<T extends CacheManager> extends BeanCreator<T> {

}
