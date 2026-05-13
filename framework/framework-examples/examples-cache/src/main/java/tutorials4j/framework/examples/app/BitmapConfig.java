package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * bitmap配置
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("bitmap")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.bitmap"})
public class BitmapConfig {

}
