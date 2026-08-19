package tutorials4j.framework.common.spring.autoconfigure;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.support.CompositeTaskDecorator;
import tutorials4j.framework.common.spring.content.SpelMethodBasedExpressionEvaluator;
import tutorials4j.framework.common.spring.core.CompositeTaskDecoratorCreator;
import tutorials4j.framework.common.spring.core.TaskDecoratorCreator;
import tutorials4j.framework.common.spring.web.GlobalExceptionHandler;

/**
 * 公共核心模块的配置类
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@Import({SpringUtil.class})
public class SpringCommonConfiguration {
  /** 初始化日志输出。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[COMMON-SPRING] Spring Common Configuration");
  }

  /**
   * 创建全局异常处理器。
   *
   * @return {@link GlobalExceptionHandler} 实例
   */
  @Bean
  GlobalExceptionHandler globalExceptionHandler() {
    log.trace("[COMMON-SPRING] Global Exception Handler");
    return new GlobalExceptionHandler();
  }

  /**
   * 创建复合任务装饰器创建器的默认 Bean。
   *
   * <p>该 Bean 会从容器中收集所有 {@link TaskDecoratorCreator} 实例， 并提供一个 {@link
   * CompositeTaskDecoratorCreator} 用于生成组合装饰器。 如果用户已经手动定义了该类型的 Bean，则此默认定义不会生效。
   *
   * @param taskDecoratorCreators 容器中所有可用的任务装饰器供应商
   * @return 复合任务装饰器创建器实例
   */
  @Bean
  @ConditionalOnMissingBean
  CompositeTaskDecoratorCreator compositeTaskDecoratorCreator(
      ObjectProvider<TaskDecoratorCreator> taskDecoratorCreators) {
    log.trace("[COMMON-SPRING] Composite Task Decorator Creator");

    List<TaskDecoratorCreator> creators =
        taskDecoratorCreators.orderedStream().collect(Collectors.toList());
    return new CompositeTaskDecoratorCreator(creators);
  }

  /**
   * 创建组合任务装饰器 Bean。
   *
   * @param creator 组合任务装饰器创建器
   * @return {@link CompositeTaskDecorator} 实例
   */
  @Bean
  @ConditionalOnMissingBean
  CompositeTaskDecorator compositeTaskDecorator(CompositeTaskDecoratorCreator creator) {
    log.trace("[COMMON-SPRING] Composite Task Decorator");
    return creator.getInstance();
  }

  /**
   * 创建基于方法的 SpEL 表达式求值器。
   *
   * @return {@link SpelMethodBasedExpressionEvaluator} 实例
   */
  @Bean
  @ConditionalOnMissingBean
  SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator() {
    log.trace("[COMMON-SPRING] Spel Method Based Expression Evaluator");
    return new SpelMethodBasedExpressionEvaluator();
  }
}
