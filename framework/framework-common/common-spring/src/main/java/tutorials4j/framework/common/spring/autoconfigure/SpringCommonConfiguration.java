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
import tutorials4j.framework.common.spring.content.SpelMethodBasedExpressionEvaluator;
import tutorials4j.framework.common.spring.core.CompositeTaskDecoratorCreator;
import tutorials4j.framework.common.spring.core.TaskDecoratorCreator;

/**
 * 公共核心模块的配置类
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@Import({SpringUtil.class})
public class SpringCommonConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[COMMON-SPRING] Common Configuration");
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
    log.debug("[COMMON-SPRING] Composite Task Decorator Creator");

    List<TaskDecoratorCreator> creators =
        taskDecoratorCreators.orderedStream().collect(Collectors.toList());
    return new CompositeTaskDecoratorCreator(creators);
  }

  @Bean
  @ConditionalOnMissingBean
  SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator() {
    return new SpelMethodBasedExpressionEvaluator();
  }
}
