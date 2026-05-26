package tutorials4j.springboot3.integration.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("ftp")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.integration.ftp"})
public class FtpConfig {}
