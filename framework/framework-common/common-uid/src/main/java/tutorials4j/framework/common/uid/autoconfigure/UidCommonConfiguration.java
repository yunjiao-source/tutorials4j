package tutorials4j.framework.common.uid.autoconfigure;

import jakarta.annotation.PostConstruct;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.spring.properties.UidProperties;
import tutorials4j.framework.common.uid.DefaultUidGeneratorCustomizer;
import tutorials4j.framework.common.uid.UidCachedGenerator;
import tutorials4j.framework.common.uid.UidDefaultedGenerator;

/**
 * 框架 UID 模块的 Spring 自动配置类。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class UidCommonConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[COMMON-UID] Uid Common Configuration");
  }

  @Bean
  UidDefaultedGenerator uidDefaultedGenerator(
      UidProperties properties, ObjectProvider<DefaultUidGeneratorCustomizer> customizers) {
    log.debug("[COMMON-UID] Uid Defaulted Generator");
    return new UidDefaultedGenerator(
        properties, customizers.orderedStream().collect(Collectors.toList()));
  }

  @Bean
  UidCachedGenerator uidCachedGenerator(
      UidProperties properties, ObjectProvider<DefaultUidGeneratorCustomizer> customizers) {
    log.debug("[COMMON-UID] Uid Cached Generator");
    return new UidCachedGenerator(
        properties, customizers.orderedStream().collect(Collectors.toList()));
  }
}
