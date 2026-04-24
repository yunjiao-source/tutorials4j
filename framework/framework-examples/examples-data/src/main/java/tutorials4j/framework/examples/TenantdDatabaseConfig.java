package tutorials4j.framework.examples;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 租户配置
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("database")
public class TenantdDatabaseConfig {
}
