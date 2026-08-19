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
 * 组合任务装饰器示例配置类。
 *
 * <p>在 {@code task} profile 下启用，开启异步支持并注册组合任务装饰器及其多个创建器， 用于演示任务执行的前后日志环绕装饰。
 *
 * @author Yun Jiao
 */
@Slf4j
@EnableAsync
@Configuration
@Profile("task")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.task"})
public class TaskConfig {
  /** 注册组合任务装饰器 Bean。 */
  @Bean
  CompositeTaskDecorator CompositeTaskDecorator(
      CompositeTaskDecoratorCreator compositeTaskDecoratorCreator) {
    return compositeTaskDecoratorCreator.getInstance();
  }

  /** 注册第一个日志环绕任务装饰器创建器。 */
  @Bean
  @Order(1)
  TaskDecoratorCreator logAroundTaskDecoratorCreator1() {
    return LogAroundTaskDecorator1::new;
  }

  /** 注册第二个日志环绕任务装饰器创建器。 */
  @Bean
  @Order(2)
  TaskDecoratorCreator logAroundTaskDecoratorCreator2() {
    return LogAroundTaskDecorator2::new;
  }

  /** 示例任务装饰器 1，在任务执行前后输出日志。 */
  public static class LogAroundTaskDecorator1 implements TaskDecorator {

    /** 包装任务，在执行前后输出日志。 */
    @Override
    public Runnable decorate(Runnable runnable) {
      return () -> {
        log.info(">>>>>>>>LogAroundTaskDecorator1 begin");
        runnable.run();
        log.info(">>>>>>>>LogAroundTaskDecorator1 end");
      };
    }
  }

  /** 示例任务装饰器 2，在任务执行前后输出日志。 */
  public static class LogAroundTaskDecorator2 implements TaskDecorator {

    /** 包装任务，在执行前后输出日志。 */
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
