package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * PowerJob 工作节点示例配置，仅在 powerjobworker 环境激活时生效， 负责扫描并加载 PowerJob 工作节点相关组件。
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("powerjobworker")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.powerjobworker"})
public class PowerjobworkerConfig {}
