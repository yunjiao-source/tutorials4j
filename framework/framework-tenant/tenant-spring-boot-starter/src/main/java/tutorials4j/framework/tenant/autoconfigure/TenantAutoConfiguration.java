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
 * 租户（tenant）模块的 Spring Boot 自动配置入口。
 *
 * <p>导入核心租户配置、缓存租户配置、Hibernate 租户配置与 MyBatis Plus 租户配置， 统一装配多租户能力。
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
  /** 初始化日志记录。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[TENANT] Tenant Auto Configuration");
  }
}
