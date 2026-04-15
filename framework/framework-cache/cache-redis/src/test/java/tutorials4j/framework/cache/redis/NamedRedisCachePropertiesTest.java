package tutorials4j.framework.cache.redis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.lang.PropertiesConsts;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 单元测试
 *
 * @author Yun Jiao
 */
public class NamedRedisCachePropertiesTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withUserConfiguration(TestConfig.class);

    // 配置类：启用目标配置属性
    @Configuration
    @EnableConfigurationProperties(NamedRedisCacheProperties.class)
    static class TestConfig {
    }

    @Test
    void testPropertiesBinding() {
        String prefix = PropertiesConsts.PROPERTY_PREFIX_CACHE + ".named-redis-caches";
        contextRunner.withPropertyValues(
                        prefix + ".user.timeToLive=4s",
                        prefix + ".user.cacheNullValues=false",
                        prefix + ".user.keyPrefix=user",
                        prefix + ".user.useKeyPrefix=false",
                        prefix + ".user.enableStatistics=true",

                        prefix + ".order.timeToLive=8s",
                        prefix + ".order.cacheNullValues=true",
                        prefix + ".order.keyPrefix=order",
                        prefix + ".order.useKeyPrefix=true",
                        prefix + ".order.enableStatistics=false"
                )
                .run(context -> {
                    NamedRedisCacheProperties properties = context.getBean(NamedRedisCacheProperties.class);
                    assertThat(properties.getNamedRedisCaches().size()).isEqualTo(2);

                    CacheProperties.Redis userProp = properties.getNamedRedisCaches().get("user");
                    assertThat(userProp.getTimeToLive()).isEqualTo(Duration.ofSeconds(4));
                    assertThat(userProp.isCacheNullValues()).isFalse();
                    assertThat(userProp.getKeyPrefix()).isEqualTo("user");
                    assertThat(userProp.isUseKeyPrefix()).isFalse();
                    assertThat(userProp.isEnableStatistics()).isTrue();

                    CacheProperties.Redis orderProp = properties.getNamedRedisCaches().get("order");
                    assertThat(orderProp.getTimeToLive()).isEqualTo(Duration.ofSeconds(8));
                    assertThat(orderProp.isCacheNullValues()).isTrue();
                    assertThat(orderProp.getKeyPrefix()).isEqualTo("order");
                    assertThat(orderProp.isUseKeyPrefix()).isTrue();
                    assertThat(orderProp.isEnableStatistics()).isFalse();
                });
    }
}
