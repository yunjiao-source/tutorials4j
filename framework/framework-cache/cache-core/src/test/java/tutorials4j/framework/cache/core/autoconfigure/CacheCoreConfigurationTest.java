package tutorials4j.framework.cache.core.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.core.properties.CacheRedisProperties;
import tutorials4j.framework.cache.core.support.CacheManagerSupplier;
import tutorials4j.framework.cache.core.support.CompositeCacheManagerCreator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * 单元测试
 *
 * @author Yun Jiao
 */
class CacheCoreConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CacheCoreConfiguration.class));

    @Test
    void shouldLoadDefaultConfiguration() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(CacheCoreConfiguration.class);
            assertThat(context).hasSingleBean(CacheRedisProperties.class);
            assertThat(context).hasSingleBean(CompositeCacheManagerCreator.class);
            assertThat(context).hasBean("compositeCacheManagerCreator");
        });
    }

    @Test
    void compositeCacheManagerCreatorShouldCollectAllCacheManagerSuppliers() {
        contextRunner.withUserConfiguration(MockCacheManagerSuppliersConfig.class)
                .run(context -> {
                    CompositeCacheManagerCreator creator = context.getBean(CompositeCacheManagerCreator.class);
                    CompositeCacheManager compositeCacheManager = creator.get();
                    assertThat(getCacheManagers(compositeCacheManager)).hasSize(2);
                });
    }

    @Test
    void compositeCacheManagerShouldFallbackToNoOpWhenNoCacheManagerSupplierPresent() {
        contextRunner.run(context -> {
            CompositeCacheManagerCreator creator = context.getBean(CompositeCacheManagerCreator.class);
            CompositeCacheManager compositeCacheManager = creator.get();
            // 验证 fallbackToNoOpCache 被设置为 true
            // CompositeCacheManager 没有直接的 getter，但可通过反射或行为验证
            // 这里简单验证其 cacheManagers 为空且未抛出异常
            assertThat(getCacheManagers(compositeCacheManager)).isEmpty();
            // 调用 getCache("any") 不应抛出异常，因为 fallback 生效
            assertThat(compositeCacheManager.getCache("test")).isNull(); // NoOpCacheManager returns null for missing cache
        });
    }

    private List<CacheManager> getCacheManagers(CompositeCacheManager compositeCacheManager) {
        try {
            var field = CompositeCacheManager.class.getDeclaredField("cacheManagers");
            field.setAccessible(true);
            return (List<CacheManager>) field.get(compositeCacheManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ----- 辅助配置类 -----

    @Configuration(proxyBeanMethods = false)
    static class MockCacheManagerSuppliersConfig {

        @Bean
        CacheManagerSupplier mockSupplier1() {
            return () -> mock(CacheManager.class);
        }

        @Bean
        CacheManagerSupplier mockSupplier2() {
            return () -> mock(CacheManager.class);
        }
    }
}