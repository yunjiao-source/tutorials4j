package tutorials4j.springboot3.schedule.app;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("remindtask")
@EnableCaching
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.schedule.remindtask"})
public class RemindtaskConfig {}
