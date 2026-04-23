package tutorials4j.framework.cache.core;

import org.springframework.cache.CacheManager;

import java.util.function.Supplier;

/**
 * {@link CacheManager} 实例提供者
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface CacheManagerSupplier extends Supplier<CacheManager> {

}
