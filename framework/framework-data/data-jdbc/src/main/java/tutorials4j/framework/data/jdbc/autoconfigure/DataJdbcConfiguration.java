package tutorials4j.framework.data.jdbc.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.data.jdbc.DataSourceRoutingManagerCreator;

import javax.sql.DataSource;

/**
 * Jdbc配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class DataJdbcConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Data |- Tenant Jdbc Configuration");
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    DataSourceRoutingManagerCreator DataSourceRoutingManagerCreator(DataSource dataSource) {
        log.debug("Tutorials4j - Data |- Data Source Routing Manager Creator");
        DataSourceRoutingManagerCreator bean = new DataSourceRoutingManagerCreator(dataSource);
        return bean;
    }

}
