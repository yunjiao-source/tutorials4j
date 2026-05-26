package tutorials4j.springboot3.web.app;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("hikariscaler")
@Configuration
@ComponentScan(
    basePackages = {
      "tutorials4j.springboot3.integration.hikariscaler",
      "tutorials4j.springboot3.jpa"
    })
public class HikariscalerConfig implements CachingConfigurer {}
