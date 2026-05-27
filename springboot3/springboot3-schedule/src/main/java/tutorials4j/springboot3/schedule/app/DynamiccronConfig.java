package tutorials4j.springboot3.schedule.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("dynamiccron")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.schedule.dynamiccron"})
public class DynamiccronConfig {}
