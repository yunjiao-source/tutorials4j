package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tutorials4j.framework.common.uid.DefaultUidGeneratorCustomizer;

/**
 * UID 生成器示例配置类。
 *
 * <p>在 {@code uid} profile 下启用，扫描并装配 UID 示例包中的组件，并自定义默认 UID 生成器的纪元时间。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@Profile("uid")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.uid"})
public class UidConfig {

  /** 注册默认 UID 生成器自定义器，设置纪元时间为 2026-05-21。 */
  @Bean
  DefaultUidGeneratorCustomizer timestampDefaultedUidGeneratorCustomizer() {
    return defaultedUidGenerator -> defaultedUidGenerator.setEpochStr("2026-05-21");
  }
}
