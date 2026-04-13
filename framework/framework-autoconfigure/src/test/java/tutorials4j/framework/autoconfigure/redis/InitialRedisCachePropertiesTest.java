package tutorials4j.framework.autoconfigure.redis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.core.constants.BasePropertiesConstants;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link InitialRedisCacheProperties} 测试用例
 *
 * @author Yun Jiao
 */
public class InitialRedisCachePropertiesTest {
    // 构建 Spring 上下文运行器，用于测试配置属性绑定
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withUserConfiguration(TestConfig.class);

    // 配置类：启用目标配置属性
    @Configuration
    @EnableConfigurationProperties(InitialRedisCacheProperties.class)
    static class TestConfig {
    }

    @Test
    void testPropertiesBinding() {
        String prefix = BasePropertiesConstants.PROPERTY_PREFIX_CACHE + ".redis";
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
                    InitialRedisCacheProperties properties = context.getBean(InitialRedisCacheProperties.class);
                    assertThat(properties.getRedis().size()).isEqualTo(2);

                    CacheProperties.Redis userProp = properties.getRedis().get("user");
                    assertThat(userProp.getTimeToLive()).isEqualTo(Duration.ofSeconds(4));
                    assertThat(userProp.isCacheNullValues()).isFalse();
                    assertThat(userProp.getKeyPrefix()).isEqualTo("user");
                    assertThat(userProp.isUseKeyPrefix()).isFalse();
                    assertThat(userProp.isEnableStatistics()).isTrue();

                    CacheProperties.Redis orderProp = properties.getRedis().get("order");
                    assertThat(orderProp.getTimeToLive()).isEqualTo(Duration.ofSeconds(8));
                    assertThat(orderProp.isCacheNullValues()).isTrue();
                    assertThat(orderProp.getKeyPrefix()).isEqualTo("order");
                    assertThat(orderProp.isUseKeyPrefix()).isTrue();
                    assertThat(orderProp.isEnableStatistics()).isFalse();
                });
    }
}
