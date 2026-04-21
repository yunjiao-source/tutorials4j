package tutorials4j.framework.examples;

import org.springframework.context.annotation.Import;
import tutorials4j.framework.data.core.tenant.TenantConfiguration;
import tutorials4j.framework.data.hibernate.tenant.HibernateTenantConfiguration;

/**
 * 应用配置
 *
 * @author Yun Jiao
 */
@Import({TenantConfiguration.class, HibernateTenantConfiguration.class})
public class AppConfig {
}
