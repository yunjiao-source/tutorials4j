package tutorials4j.springcloud.gateway.app;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * demo1 环境的网关配置：启用服务发现并扫描 demo1 示例包下的组件，同时注册带配置前缀的 NacosConfigProperties Bean。
 *
 * @author Yun Jiao
 */
@Profile("demo1")
@EnableDiscoveryClient
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springcloud.gateway.demo1"})
public class Demo1Profile {

  /**
   * 注册绑定 {@code spring.cloud.nacos.config} 配置前缀的 NacosConfigProperties Bean。
   *
   * <p>NacosConfigBootstrapConfiguration 配置中创建的示例没有指定属性前缀，造成 yml 中的配置属性无法注入，如：namespace。
   *
   * @return Nacos 配置属性实例
   */
  @Bean
  @ConfigurationProperties("spring.cloud.nacos.config")
  public NacosConfigProperties nacosConfigProperties() {
    return new NacosConfigProperties();
  }
}
