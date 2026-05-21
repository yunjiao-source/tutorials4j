package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tutorials4j.framework.common.uid.DefaultUidGeneratorCustomizer;

/**
 * 组合任务装饰器配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@Profile("uid")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.uid"})
public class UidConfig {

  @Bean
  DefaultUidGeneratorCustomizer timestampDefaultedUidGeneratorCustomizer() {
    return defaultedUidGenerator -> defaultedUidGenerator.setEpochStr("2026-05-21");
  }
}
