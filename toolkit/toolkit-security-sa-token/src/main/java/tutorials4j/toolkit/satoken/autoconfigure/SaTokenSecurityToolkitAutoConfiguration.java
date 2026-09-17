package tutorials4j.toolkit.satoken.autoconfigure;

import io.micrometer.tracing.Tracer;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tutorials4j.toolkit.core.constant.PropertyConsts;
import tutorials4j.toolkit.satoken.SaLogForSlf4j;
import tutorials4j.toolkit.satoken.func.CheckLoginSaParamFunction;
import tutorials4j.toolkit.satoken.func.CompositeSaParamFunction;
import tutorials4j.toolkit.satoken.func.OrderedSaParamFunction;
import tutorials4j.toolkit.satoken.strategy.BlockUrlsSaFilterAuthStrategy;
import tutorials4j.toolkit.satoken.strategy.CheckLoginSaFilterAuthStrategy;
import tutorials4j.toolkit.satoken.strategy.CompositeFilterAuthStrategy;
import tutorials4j.toolkit.satoken.strategy.LoggingSaFilterAuthStrategy;
import tutorials4j.toolkit.satoken.strategy.PointcutSaFilterAuthStrategy;
import tutorials4j.toolkit.satoken.strategy.SimpleSaFilterErrorStrategy;
import tutorials4j.toolkit.satoken.strategy.WhiteUrlsSaFilterAuthStrategy;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({
  SaTokenSecurityToolkitProperties.class,
})
public class SaTokenSecurityToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-SECURITY-SA-TOKEN] Sa-Token Security Toolkit Auto Configuration");
  }

  @Bean
  SaLogForSlf4j saLogForSlf4j() {
    log.trace("[TOOLKIT-SECURITY-SA-TOKEN] Sa Log For Slf4j");
    return new SaLogForSlf4j();
  }

  @Bean
  SimpleSaFilterErrorStrategy simpleSaFilterErrorStrategy(Tracer tracer) {
    log.trace("[TOOLKIT-SECURITY-SA-TOKEN] Simple Sa Filter Error Strategy");
    return new SimpleSaFilterErrorStrategy(tracer);
  }

  @Bean
  CompositeFilterAuthStrategy compositeFilterAuthStrategy(
      ObjectProvider<PointcutSaFilterAuthStrategy> strategies) {
    log.trace("[TOOLKIT-SECURITY-SA-TOKEN] Composite Filter Auth Strategy");
    List<PointcutSaFilterAuthStrategy> list = strategies.orderedStream().toList();
    if (!list.isEmpty()) {
      log.trace(
          "[TOOLKIT-SECURITY-SA-TOKEN] 认证策略注入：{}",
          list.stream()
              .map(PointcutSaFilterAuthStrategy::getClass)
              .map(Class::getName)
              .collect(Collectors.joining(";")));
    }
    return new CompositeFilterAuthStrategy(list);
  }

  @Bean
  CompositeSaParamFunction compositeSaParamFunction(
      ObjectProvider<OrderedSaParamFunction> functions) {
    log.trace("[TOOLKIT-SECURITY-SA-TOKEN] Composite Sa Param Function");
    List<OrderedSaParamFunction> list = functions.orderedStream().toList();
    if (!list.isEmpty()) {
      log.trace(
          "[TOOLKIT-SECURITY-SA-TOKEN] 认证函数注入：{}",
          list.stream()
              .map(OrderedSaParamFunction::getClass)
              .map(Class::getName)
              .collect(Collectors.joining(";")));
    }
    return new CompositeSaParamFunction(list);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = PropertyConsts.PROPERTY_TOOLKIT_SECURITY_SA_TOKEN,
      name = "interceptor.check-login",
      havingValue = "true")
  CheckLoginSaParamFunction checkLoginSaParamFunction() {
    log.trace("[TOOLKIT-SECURITY-SA-TOKEN] Check Login Sa Param Function");
    return new CheckLoginSaParamFunction();
  }

  @Bean
  BlockUrlsSaFilterAuthStrategy blockUrlsSaFilterAuthStrategy(
      SaTokenSecurityToolkitProperties properties) {
    log.trace("[TOOLKIT-SECURITY-SA-TOKEN] Block Urls Sa Filter Auth Strategy");
    return new BlockUrlsSaFilterAuthStrategy(properties.getFilter().getBlockUrls());
  }

  @Bean
  CheckLoginSaFilterAuthStrategy checkLoginSaFilterAuthStrategy() {
    log.trace("[TOOLKIT-SECURITY-SA-TOKEN] Check Login Sa Filter Auth Strategy");
    return new CheckLoginSaFilterAuthStrategy();
  }

  @Bean
  LoggingSaFilterAuthStrategy loggingSaFilterAuthStrategy() {
    log.trace("[TOOLKIT-SECURITY-SA-TOKEN] Logging Sa Filter Auth Strategy");
    return new LoggingSaFilterAuthStrategy();
  }

  @Bean
  WhiteUrlsSaFilterAuthStrategy whiteUrlsSaFilterAuthStrategy(
      SaTokenSecurityToolkitProperties properties) {
    log.trace("[TOOLKIT-SECURITY-SA-TOKEN] White Urls Sa Filter Auth Strategy");
    return new WhiteUrlsSaFilterAuthStrategy(properties.getFilter().getWhiteUrls());
  }
}
