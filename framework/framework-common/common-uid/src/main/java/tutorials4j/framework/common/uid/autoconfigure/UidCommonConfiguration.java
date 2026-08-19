package tutorials4j.framework.common.uid.autoconfigure;

import jakarta.annotation.PostConstruct;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.uid.DefaultUidGeneratorCustomizer;
import tutorials4j.framework.common.uid.UidCachedGenerator;
import tutorials4j.framework.common.uid.UidDefaultedGenerator;

/**
 * 框架 UID 模块的 Spring 自动配置类。
 *
 * <p>注册默认（实时计算）模式与缓存模式两种 UID 生成器 Bean， 并允许通过 {@link DefaultUidGeneratorCustomizer} 定制生成器的构建过程。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({UidCommonProperties.class})
public class UidCommonConfiguration {
  /** 初始化日志输出，应用启动后执行。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[COMMON-UID] Uid Common Configuration");
  }

  /**
   * 注册默认（实时计算）模式 UID 生成器 Bean。
   *
   * @param properties UID 生成器通用配置属性
   * @param customizers 用户提供的定制器（按顺序）
   * @return 默认模式 UID 生成器实例
   */
  @Bean
  UidDefaultedGenerator uidDefaultedGenerator(
      UidCommonProperties properties, ObjectProvider<DefaultUidGeneratorCustomizer> customizers) {
    log.trace("[COMMON-UID] Uid Defaulted Generator");
    return new UidDefaultedGenerator(
        properties, customizers.orderedStream().collect(Collectors.toList()));
  }

  /**
   * 注册缓存模式 UID 生成器 Bean。
   *
   * @param properties UID 生成器通用配置属性
   * @param customizers 用户提供的定制器（按顺序）
   * @return 缓存模式 UID 生成器实例
   */
  @Bean
  UidCachedGenerator uidCachedGenerator(
      UidCommonProperties properties, ObjectProvider<DefaultUidGeneratorCustomizer> customizers) {
    log.trace("[COMMON-UID] Uid Cached Generator");
    return new UidCachedGenerator(
        properties, customizers.orderedStream().collect(Collectors.toList()));
  }
}
