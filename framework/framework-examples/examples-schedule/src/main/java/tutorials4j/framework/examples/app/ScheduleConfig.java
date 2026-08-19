package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring 定时任务示例配置，仅在 schedule 环境激活时生效， 开启异步支持与定时调度，并扫描定时任务示例相关组件。
 *
 * @author Yun Jiao
 */
@EnableAsync
@EnableScheduling
@Configuration
@Profile("schedule")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.schedule"})
public class ScheduleConfig {}
