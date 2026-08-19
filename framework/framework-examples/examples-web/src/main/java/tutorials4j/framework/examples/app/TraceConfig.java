package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 链路追踪示例模块的配置类，开启异步支持并在 trace profile 下生效，负责扫描链路追踪示例相关组件。
 *
 * @author Yun Jiao
 */
@Slf4j
@EnableAsync
@Configuration
@Profile("trace")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.trace"})
public class TraceConfig {
  /**
   * 创建指向 jsonplaceholder.typicode.com 的 WebClient，供链路追踪示例使用。
   *
   * @param builder WebClient 构建器
   * @return 配置好基础地址的 WebClient
   */
  @Bean
  WebClient webClient(WebClient.Builder builder) {
    return builder.baseUrl("https://jsonplaceholder.typicode.com").build();
  }
}
