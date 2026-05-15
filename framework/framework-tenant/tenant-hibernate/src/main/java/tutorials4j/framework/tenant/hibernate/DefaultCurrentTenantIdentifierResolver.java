package tutorials4j.framework.tenant.hibernate;

import java.util.Map;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import tutorials4j.framework.common.core.TenantContextHolder;

/**
 * Hibernate 多租户标识解析器的默认实现。
 *
 * <p>该类负责从当前线程上下文中获取租户标识符（tenant identifier）， 并将其提供给 Hibernate 用于多租户数据源的切换。 同时实现了 {@link
 * HibernatePropertiesCustomizer} 接口， 自动将自身注册到 Hibernate 的多租户配置中。
 *
 * @author Yun Jiao
 * @see CurrentTenantIdentifierResolver
 * @see HibernatePropertiesCustomizer
 */
public class DefaultCurrentTenantIdentifierResolver
    implements CurrentTenantIdentifierResolver<String>, HibernatePropertiesCustomizer {
  @Override
  public String resolveCurrentTenantIdentifier() {
    return TenantContextHolder.get();
  }

  @Override
  public boolean validateExistingCurrentSessions() {
    return true;
  }

  /**
   * 将当前解析器注册到 Hibernate 的配置属性中。
   *
   * @param hibernateProperties Hibernate 属性集合，会被 Spring Boot 的 JPA 属性合并
   */
  @Override
  public void customize(Map<String, Object> hibernateProperties) {
    hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
  }
}
