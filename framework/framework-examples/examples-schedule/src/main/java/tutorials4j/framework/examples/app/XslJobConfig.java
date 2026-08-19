package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * XXL-Job 定时任务示例配置，仅在 xxl-job 环境激活时生效， 负责扫描并加载 XXL-Job 示例相关组件。
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("xxl-job")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.xxljob"})
public class XslJobConfig {}
