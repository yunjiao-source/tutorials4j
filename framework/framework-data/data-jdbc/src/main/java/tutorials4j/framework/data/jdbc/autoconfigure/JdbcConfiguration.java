package tutorials4j.framework.data.jdbc.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
public class JdbcConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("[DATA-JDBC] Data Jdbc Configuration");
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnMissingBean
    DataSourceRoutingManagerCreator dataSourceRoutingManagerCreator(DataSource dataSource) {
        log.debug("[DATA-JDBC] Data Source Routing Manager Creator");
        return new DataSourceRoutingManagerCreator(dataSource);
    }

}
