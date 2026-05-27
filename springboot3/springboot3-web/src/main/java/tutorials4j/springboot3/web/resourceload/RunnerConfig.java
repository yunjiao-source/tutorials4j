package tutorials4j.springboot3.web.resourceload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置
 *
 * @author yangyunjiao
 */
@Slf4j
@Configuration
public class RunnerConfig {

  @Bean
  public CommandLineRunner valueDemoServiceRunner(ValueDemoService valueDemoService) {
    return args -> {
      log.info(">>> 使用 ValueDemoService：");
      valueDemoService.print();
    };
  }

  @Bean
  public CommandLineRunner resourceLoaderDemoServiceRunner(
      ResourceLoaderDemoService resourceLoaderDemoService) {
    return args -> {
      log.info(">>> 使用 ResourceLoaderDemoService：");
      resourceLoaderDemoService.print();
    };
  }

  @Bean
  public CommandLineRunner resourcePatternResolverDemoServiceRunner(
      ResourcePatternResolverDemoService resourcePatternResolverDemoService) {
    return args -> {
      log.info(">>> 使用 ResourcePatternResolverDemoService：");
      resourcePatternResolverDemoService.print();
    };
  }
}
