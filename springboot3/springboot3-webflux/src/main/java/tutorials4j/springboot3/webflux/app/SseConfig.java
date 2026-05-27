package tutorials4j.springboot3.webflux.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("sse")
@EnableScheduling
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.webflux.sse"})
public class SseConfig {}
