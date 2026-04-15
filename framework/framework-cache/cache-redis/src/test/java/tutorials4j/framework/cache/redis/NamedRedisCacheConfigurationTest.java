package tutorials4j.framework.cache.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 单元测试
 *
 * @author Yun Jiao
 */
public class NamedRedisCacheConfigurationTest {
    private ApplicationContextRunner applicationContextRunner;

    @BeforeEach
    public void setUp() {
        applicationContextRunner = new ApplicationContextRunner()
                .withUserConfiguration(NamedRedisCacheConfiguration.class);
    }

    @Test
    public void beanExist() {
        applicationContextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(NamedRedisCacheManagerBuilderCustomizer.class);
                    assertThat(context).hasSingleBean(NamedRedisCacheManagerCustomizer.class);
                });
    }
}
