package tutorials4j.framework.cache.core.properties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 单元测试
 *
 * @author Yun Jiao
 */
public class CacheRedisPropertiesTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withUserConfiguration(TestConfig.class);

    // 配置类：启用目标配置属性
    @Configuration
    @EnableConfigurationProperties(CacheRedisProperties.class)
    static class TestConfig {
    }

    @Test
    void testPropertiesBinding() {
        String prefix = PropertiesConsts.PROPERTY_PREFIX_CACHE_REDIS ;
        contextRunner.withPropertyValues(

                        prefix + ".named-caches.user.timeToLive=4s",
                        prefix + ".named-caches.user.cacheNullValues=false",
                        prefix + ".named-caches.user.keyPrefix=user",
                        prefix + ".named-caches.user.useKeyPrefix=false",
                        prefix + ".named-caches.user.enableStatistics=true",

                        prefix + ".named-caches.order.timeToLive=6s",
                        prefix + ".named-caches.order.cacheNullValues=true",
                        prefix + ".named-caches.order.keyPrefix=order",
                        prefix + ".named-caches.order.useKeyPrefix=true",
                        prefix + ".named-caches.order.enableStatistics=false"
                )
                .run(context -> {
                    CacheRedisProperties properties = context.getBean(CacheRedisProperties.class);
                    assertThat(properties.getNamedCaches().size()).isEqualTo(2);

                    CacheProperties.Redis userProp = properties.getNamedCaches().get("user");
                    assertThat(userProp.getTimeToLive()).isEqualTo(Duration.ofSeconds(4));
                    assertThat(userProp.isCacheNullValues()).isFalse();
                    assertThat(userProp.getKeyPrefix()).isEqualTo("user");
                    assertThat(userProp.isUseKeyPrefix()).isFalse();
                    assertThat(userProp.isEnableStatistics()).isTrue();

                    CacheProperties.Redis orderProp = properties.getNamedCaches().get("order");
                    assertThat(orderProp.getTimeToLive()).isEqualTo(Duration.ofSeconds(6));
                    assertThat(orderProp.isCacheNullValues()).isTrue();
                    assertThat(orderProp.getKeyPrefix()).isEqualTo("order");
                    assertThat(orderProp.isUseKeyPrefix()).isTrue();
                    assertThat(orderProp.isEnableStatistics()).isFalse();
                });
    }
}
