package tutorials4j.framework.examples.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import tutorials4j.framework.common.core.task.TaskDecoratorSupplier;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@EnableAsync
@Configuration
public class CompositeTaskDecoratorConfig {
    @Bean
    TaskDecoratorSupplier logTaskDecoratorSupplier() {
        return LogTaskDecorator::new;
    }

    public static class LogTaskDecorator implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {
            return () -> {
                log.info(">>>>>>>>LogTaskDecorator");
                runnable.run();
            };
        }
    }
}
