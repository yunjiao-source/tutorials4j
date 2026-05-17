package tutorials4j.framework.tenant.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.tenant.cache.autoconfigure.CacheTenantConfiguration;
import tutorials4j.framework.tenant.core.autoconfigure.TenantConfiguration;
import tutorials4j.framework.tenant.hibernate.autoconfigure.HibernateTenantConfiguration;
import tutorials4j.framework.tenant.mybatis.autoconfigure.MybatisPlusTenantConfiguration;

/**
 * 租户（tenant）模块自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  TenantConfiguration.class,
  CacheTenantConfiguration.class,
  HibernateTenantConfiguration.class,
  MybatisPlusTenantConfiguration.class
})
public class TenantAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[TENANT] Tenant Auto Configuration");
  }
}
