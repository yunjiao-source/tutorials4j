package tutorials4j.framework.examples;

import org.springframework.context.annotation.Import;
import tutorials4j.framework.data.core.autoconfigure.DataTenantConfiguration;
import tutorials4j.framework.data.hibernate.autoconfigure.DataHibernateTenantConfiguration;

/**
 * 应用配置
 *
 * @author Yun Jiao
 */
@Import({DataTenantConfiguration.class, DataHibernateTenantConfiguration.class})
public class AppConfig {
}
