package tutorials4j.springboot3.web.app;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("httpexchange")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.web.httpexchange"})
public class HttpExchangeConfig {
  @Bean
  public HttpExchangeRepository httpExchangeRepository() {
    InMemoryHttpExchangeRepository repository = new InMemoryHttpExchangeRepository();
    // 可选：修改内存中存储的记录条数，默认为100
    repository.setCapacity(1000);
    return repository;
  }
}
