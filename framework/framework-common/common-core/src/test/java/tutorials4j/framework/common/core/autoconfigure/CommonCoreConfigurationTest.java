package tutorials4j.framework.common.core.autoconfigure;

import cn.hutool.extra.spring.SpringUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.CompositeTaskDecorator;
import tutorials4j.framework.common.core.task.CompositeTaskDecoratorCreator;
import tutorials4j.framework.common.core.task.TaskDecoratorSupplier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 单元测试
 *
 * @author Yun Jiao
 */
public class CommonCoreConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonCoreConfiguration.class));

    @Test
    public void testSpringUtilInitSuccess() {
        contextRunner
                .withUserConfiguration(TestUserServiceConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(TestUserServiceConfiguration.UserService.class);
                    TestUserServiceConfiguration.UserService userService = SpringUtil.getBean(TestUserServiceConfiguration.UserService.class);
                    assertThat(userService).isNotNull();

                });
    }

    @Test
    void shouldCreateCompositeTaskDecoratorCreatorByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(CompositeTaskDecoratorCreator.class);
            assertThat(context.getBean(CompositeTaskDecoratorCreator.class)).isNotNull();
        });
    }

    @Test
    void shouldNotCreateCompositeTaskDecoratorCreatorWhenBeanAlreadyExists() {
        contextRunner.withUserConfiguration(CustomCreatorConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(CompositeTaskDecoratorCreator.class);
                    assertThat(context.getBean(CompositeTaskDecoratorCreator.class))
                            .isInstanceOf(CustomCompositeTaskDecoratorCreator.class);
                });
    }

    @Test
    void compositeTaskDecoratorCreatorShouldCollectAndReverseDecorators() {
        contextRunner.withUserConfiguration(TestDecoratorSuppliersConfig.class)
                .run(context -> {
                    CompositeTaskDecoratorCreator creator = context.getBean(CompositeTaskDecoratorCreator.class);
                    CompositeTaskDecorator compositeDecorator = creator.get();

                    // Extract internal decorator list via reflection for verification
                    List<TaskDecorator> decorators = getDecoratorList(compositeDecorator);

                    // Suppliers with order: FIRST (order=1), SECOND (order=2), THIRD (order=3)
                    // Expected reversed list: THIRD, SECOND, FIRST
                    assertThat(decorators).hasSize(3);
                    assertThat(decorators.get(0)).isInstanceOf(ThirdDecorator.class);
                    assertThat(decorators.get(1)).isInstanceOf(SecondDecorator.class);
                    assertThat(decorators.get(2)).isInstanceOf(FirstDecorator.class);
                });
    }

    @Test
    void compositeTaskDecoratorCreatorShouldHandleEmptySuppliers() {
        contextRunner.run(context -> {
            CompositeTaskDecoratorCreator creator = context.getBean(CompositeTaskDecoratorCreator.class);
            CompositeTaskDecorator compositeDecorator = creator.get();
            List<TaskDecorator> decorators = getDecoratorList(compositeDecorator);
            assertThat(decorators).isEmpty();
        });
    }

    // Helper to extract decorator list from CompositeTaskDecorator
    private List<TaskDecorator> getDecoratorList(CompositeTaskDecorator compositeDecorator) {
        try {
            var field = CompositeTaskDecorator.class.getDeclaredField("taskDecorators");
            field.setAccessible(true);
            return (List<TaskDecorator>) field.get(compositeDecorator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ----- Custom configuration for overriding default creator -----
    @Configuration
    static class CustomCreatorConfig {
        @Bean
        CompositeTaskDecoratorCreator customCompositeTaskDecoratorCreator() {
            return new CustomCompositeTaskDecoratorCreator();
        }
    }

    static class CustomCompositeTaskDecoratorCreator extends CompositeTaskDecoratorCreator {
        CustomCompositeTaskDecoratorCreator() {
            super(null); // not used in test
        }
    }

    // ----- Test decorator suppliers with explicit order -----
    @Configuration
    static class TestDecoratorSuppliersConfig {

        @Bean
        @Order(1)
        TaskDecoratorSupplier firstSupplier() {
            return () -> new FirstDecorator();
        }

        @Bean
        @Order(2)
        TaskDecoratorSupplier secondSupplier() {
            return () -> new SecondDecorator();
        }

        @Bean
        @Order(3)
        TaskDecoratorSupplier thirdSupplier() {
            return () -> new ThirdDecorator();
        }

    }

    static class TestRunner implements Runnable {
        @Override
        public void run() {

        }
    }
    // Simple decorator implementations for identity tracking
    static class FirstDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            return runnable;
        }
    }

    static class SecondDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            return runnable;
        }
    }

    static class ThirdDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            return runnable;
        }
    }
    @Configuration
    static class TestUserServiceConfiguration {
        @Bean
        public UserService userService() {
            return new UserService();
        }

        static class UserService {

        }
    }
}
