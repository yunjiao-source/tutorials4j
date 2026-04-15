package tutorials4j.framework.common.core;

import cn.hutool.extra.spring.SpringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 单元测试
 *
 * @author Yun Jiao
 */
public class CommonCoreConfigurationTest {
    private ApplicationContextRunner applicationContextRunner;

    @BeforeEach
    public void setUp() {
        applicationContextRunner = new ApplicationContextRunner()
                .withUserConfiguration(CommonCoreConfiguration.class);
    }

    @Test
    public void testSpringUtilInitSuccess() {
        applicationContextRunner
                .withUserConfiguration(ConfigurerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ConfigurerConfiguration.UserService.class);
                    ConfigurerConfiguration.UserService userService = SpringUtil.getBean(ConfigurerConfiguration.UserService.class);
                    assertThat(userService).isNotNull();

                });
    }

    @Configuration
    static class ConfigurerConfiguration {
        @Bean
        public UserService userService() {
            return new UserService();
        }

        static class UserService {

        }
    }
}
