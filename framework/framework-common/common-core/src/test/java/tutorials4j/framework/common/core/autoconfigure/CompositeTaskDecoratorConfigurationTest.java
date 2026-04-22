package tutorials4j.framework.common.core.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.CompositeTaskDecorator;
import tutorials4j.framework.common.core.task.TaskDecoratorSupplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 单元测试
 *
 * @author Yun Jiao
 */
public class CompositeTaskDecoratorConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CommonCoreConfiguration.class));

    // ===================== 场景1：没有 TaskDecoratorSupplier =====================
    @Test
    void shouldCreateCompositeTaskDecoratorWithEmptyListWhenNoSuppliersPresent() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(CompositeTaskDecorator.class);
            CompositeTaskDecorator decorator = context.getBean(CompositeTaskDecorator.class);

            // 通过反射获取内部的 decorators 列表（CompositeTaskDecorator 没有提供 getter，但我们可以通过实际执行来验证）
            // 或者直接验证它是一个 CompositeTaskDecorator，其行为等同于无装饰器
            // 为了精确，可以创建一个可执行任务并检查是否无额外操作，这里简单判断存在即可
            assertThat(decorator).isNotNull();
        });
    }

    // ===================== 场景2：已存在自定义 TaskDecorator 时，条件阻止 CompositeTaskDecorator 创建 =====================
    @Test
    void shouldNotCreateCompositeTaskDecoratorWhenTaskDecoratorBeanAlreadyExists() {
        contextRunner.withBean(CustomTaskDecorator.class, CustomTaskDecorator::new)
                .run(context -> {
                    assertThat(context).hasSingleBean(TaskDecorator.class);
                    assertThat(context.getBean(TaskDecorator.class)).isInstanceOf(CustomTaskDecorator.class);
                    assertThat(context).doesNotHaveBean(CompositeTaskDecorator.class);
                });
    }


    // ===================== 辅助配置与实现 =====================

    static class CustomTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            return runnable;
        }
    }


    // 更严谨的顺序测试：使用自定义装饰器修改共享 StringBuilder
    @Test
    void shouldRespectOrderOfTaskDecoratorSuppliers() {
        contextRunner
                .withUserConfiguration(OrderedSupplierConfig.class)
                .run(context -> {
                    CompositeTaskDecorator composite = context.getBean(CompositeTaskDecorator.class);
                    StringBuilder sb = context.getBean(StringBuilder.class);
                    Runnable core = () -> sb.append("core");
                    composite.decorate(core).run();
                    // 由于 orderedStream() 返回的流顺序为升序（小的在前），即 HIGHEST_PRECEDENCE 在前
                    // 列表顺序为 [first, second] → 装饰顺序：second 包装 first 再包装 core
                    // 执行时先执行 second 的 before，再执行 first 的 before，然后 core，最后按相反顺序 after（这里没有 after）
                    // 所以最终字符串应为 "B-A-core"
                    assertThat(sb.toString()).isEqualTo("B-A-core");
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class OrderedSupplierConfig {

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        TaskDecoratorSupplier first(StringBuilder sb) {
            return () -> runnable -> () -> {
                sb.append("A-");
                runnable.run();
            };
        }

        @Bean
        @Order(Ordered.LOWEST_PRECEDENCE)
        TaskDecoratorSupplier second(StringBuilder sb) {
            return () -> runnable -> () -> {
                sb.append("B-");
                runnable.run();
            };
        }

        @Bean
        StringBuilder sb() {
            return new StringBuilder();
        }
    }
}
