package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@EnableAsync
@EnableScheduling
@Configuration
@Profile("schedule")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.schedule"})
public class ScheduleConfig {}
