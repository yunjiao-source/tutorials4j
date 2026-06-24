package tutorials4j.springcloud.gateway.app;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("demo1")
@EnableDiscoveryClient
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springcloud.gateway.demo1"})
public class Demo1Profile {

  /**
   * NacosConfigBootstrapConfiguration配置中创建的示例没有指定属性前缀，造成yml中的配置属性无法注入，如：namespace
   *
   * @return
   */
  @Bean
  @ConfigurationProperties("spring.cloud.nacos.config")
  public NacosConfigProperties nacosConfigProperties() {
    return new NacosConfigProperties();
  }
}
