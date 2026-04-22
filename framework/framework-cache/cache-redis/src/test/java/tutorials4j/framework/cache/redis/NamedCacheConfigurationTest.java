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
public class NamedCacheConfigurationTest {
    private ApplicationContextRunner applicationContextRunner;

    @BeforeEach
    public void setUp() {
        applicationContextRunner = new ApplicationContextRunner()
                .withUserConfiguration(NamedCacheConfiguration.class);
    }

    @Test
    public void beanExist() {
        applicationContextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(NamedCacheManagerBuilderCustomizer.class);
                    assertThat(context).hasSingleBean(NamedCacheManagerCustomizer.class);
                });
    }
}
