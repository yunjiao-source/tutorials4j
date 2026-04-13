package tutorials4j.framework.autoconfigure.redis;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.autoconfigure.servlet.CachedBodyConfig;

import java.lang.annotation.*;

/**
 * {@link CachedBodyConfig} 配置开启
 *
 * @author Yun Jiao
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableCaching
@Import(InitialRedisCacheConfig.class)
public @interface EnableInitialRedisCache {
}
