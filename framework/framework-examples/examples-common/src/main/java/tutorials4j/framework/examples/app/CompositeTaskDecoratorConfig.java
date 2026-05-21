package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import tutorials4j.framework.common.spring.core.CompositeTaskDecoratorCreator;
import tutorials4j.framework.common.spring.core.TaskDecoratorCreator;

/**
 * 组合任务装饰器配置
 *
 * @author Yun Jiao
 */
@Slf4j
@EnableAsync
@Configuration
@Profile("task")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.task"})
public class CompositeTaskDecoratorConfig {
  @Bean
  CompositeTaskDecorator CompositeTaskDecorator(
      CompositeTaskDecoratorCreator compositeTaskDecoratorCreator) {
    return compositeTaskDecoratorCreator.getInstance();
  }

  @Bean
  @Order(1)
  TaskDecoratorCreator logAroundTaskDecoratorCreator1() {
    return LogAroundTaskDecorator1::new;
  }

  @Bean
  @Order(2)
  TaskDecoratorCreator logAroundTaskDecoratorCreator2() {
    return LogAroundTaskDecorator2::new;
  }

  public static class LogAroundTaskDecorator1 implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
      return () -> {
        log.info(">>>>>>>>LogAroundTaskDecorator1 begin");
        runnable.run();
        log.info(">>>>>>>>LogAroundTaskDecorator1 end");
      };
    }
  }

  public static class LogAroundTaskDecorator2 implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
      return () -> {
        log.info(">>>>>>>>LogAroundTaskDecorator2 begin");
        runnable.run();
        log.info(">>>>>>>>LogAroundTaskDecorator2 end");
      };
    }
  }
}
