package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tutorials4j.framework.feature.schedule.annotation.EnableScheduleFeature;

/**
 * 调度特性示例的配置类。
 *
 * <p>启用异步、定时调度与调度特性支持，并在 {@code schedule} Profile 下扫描 {@code
 * tutorials4j.framework.examples.schedule} 包中的示例任务。
 *
 * @author Yun Jiao
 */
@EnableAsync
@EnableScheduling
@EnableScheduleFeature
@Configuration
@Profile("schedule")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.schedule"})
public class ScheduleConfig {}
