package tutorials4j.framework.data.hibernate.tenant;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 共享表租户配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class HibernateTableTenantConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Hibernate Table Tenant Configuration");
    }

    @Bean
    DefaultCurrentTenantIdentifierResolver defaultCurrentTenantIdentifierResolver() {
        log.debug("Tutorials4j |- Default Current Tenant Identifier Resolver");
        return new DefaultCurrentTenantIdentifierResolver();
    }
}
