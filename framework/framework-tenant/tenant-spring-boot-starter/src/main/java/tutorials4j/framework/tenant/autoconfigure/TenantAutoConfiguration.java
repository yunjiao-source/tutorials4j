package tutorials4j.framework.tenant.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.tenant.cache.autoconfigure.CacheConfiguration;
import tutorials4j.framework.tenant.core.autoconfigure.TenantConfiguration;
import tutorials4j.framework.tenant.hibernate.autoconfigure.HibernateConfiguration;
import tutorials4j.framework.tenant.mybatis.autoconfigure.MybatisPlusConfiguration;

/**
 * 租户（tenant）模块自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  TenantConfiguration.class,
  CacheConfiguration.class,
  HibernateConfiguration.class,
  MybatisPlusConfiguration.class
})
public class TenantAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[TENANT] Tenant Auto Configuration");
  }
}
