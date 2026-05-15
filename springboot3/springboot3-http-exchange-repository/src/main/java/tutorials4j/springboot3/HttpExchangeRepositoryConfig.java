package tutorials4j.springboot3;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Configuration
public class HttpExchangeRepositoryConfig {
  @Bean
  public HttpExchangeRepository httpExchangeRepository() {
    InMemoryHttpExchangeRepository repository = new InMemoryHttpExchangeRepository();
    // 可选：修改内存中存储的记录条数，默认为100
    repository.setCapacity(1000);
    return repository;
  }
}
