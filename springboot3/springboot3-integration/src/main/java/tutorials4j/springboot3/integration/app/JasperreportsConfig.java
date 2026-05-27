package tutorials4j.springboot3.integration.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Profile("jasperreports")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.integration.jasperreports"})
public class JasperreportsConfig {}
