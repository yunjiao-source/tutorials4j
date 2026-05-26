package tutorials4j.springboot3.web.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import tutorials4j.springboot3.common.JpaCommonConfiguration;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("hikariscaler")
@Configuration
@Import(JpaCommonConfiguration.class)
@ComponentScan(
    basePackages = {
      "tutorials4j.springboot3.web.hikariscaler",
    })
public class HikariscalerConfig {}
