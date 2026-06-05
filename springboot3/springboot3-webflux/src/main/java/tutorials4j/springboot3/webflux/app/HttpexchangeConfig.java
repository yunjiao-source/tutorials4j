package tutorials4j.springboot3.webflux.app;

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
@ComponentScan(basePackages = {"tutorials4j.springboot3.webflux.httpexchange"})
public class HttpexchangeConfig {}
