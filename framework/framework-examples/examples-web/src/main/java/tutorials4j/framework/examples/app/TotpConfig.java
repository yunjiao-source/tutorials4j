package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * TOTP 动态口令示例模块的配置类，仅在 totp profile 下生效，负责扫描 TOTP 示例相关组件。
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("totp")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.totp"})
public class TotpConfig {}
