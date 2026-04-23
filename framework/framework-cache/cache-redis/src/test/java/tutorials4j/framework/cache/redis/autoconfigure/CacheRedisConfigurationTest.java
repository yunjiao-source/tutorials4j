package tutorials4j.framework.cache.redis.autoconfigure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tutorials4j.framework.cache.core.autoconfigure.CacheCoreConfiguration;
import tutorials4j.framework.cache.redis.NamedRedisCacheManagerBuilderCustomizer;
import tutorials4j.framework.cache.redis.NamedCacheManagerCustomizer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 单元测试
 *
 * @author Yun Jiao
 */
public class CacheRedisConfigurationTest {
    private ApplicationContextRunner applicationContextRunner;

    @BeforeEach
    public void setUp() {
        applicationContextRunner = new ApplicationContextRunner()
                .withUserConfiguration(CacheCoreConfiguration.class,CacheRedisConfiguration.class);
    }

    @Test
    public void beanExist() {
        applicationContextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(NamedRedisCacheManagerBuilderCustomizer.class);
                    assertThat(context).hasSingleBean(NamedCacheManagerCustomizer.class);
                });
    }
}
