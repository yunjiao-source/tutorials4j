package tutorials4j.framework.data.hibernate;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import tutorials4j.framework.data.core.tenant.TenantContextHolder;

import java.util.Map;

/**
 * {@link CurrentTenantIdentifierResolver} 默认实现
 *
 * @author Yun Jiao
 */
public class DefaultCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver<String>, HibernatePropertiesCustomizer {
    @Override
    public String resolveCurrentTenantIdentifier() {
        return TenantContextHolder.get();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }
}
