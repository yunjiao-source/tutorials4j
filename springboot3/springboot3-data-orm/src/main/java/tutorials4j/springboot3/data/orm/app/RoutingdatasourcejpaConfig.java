package tutorials4j.springboot3.data.orm.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("routingdatasourcejpa")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.data.orm.routingdatasourcejpa"})
public class RoutingdatasourcejpaConfig {}
