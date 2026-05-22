package tutorials4j.framework.data.jdbc.autoconfigure;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.data.jdbc.routing.DataSourceRoutingManagerCreator;
import tutorials4j.framework.data.jdbc.routing.MultipleRoutingDataSource;

/**
 * Jdbc配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class JdbcDataConfiguration {
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

  @Bean
  @ConditionalOnMissingBean
  MultipleRoutingDataSource MultipleRoutingDataSource(DataSourceRoutingManagerCreator creator) {
    log.debug("[DATA-JDBC] Multiple Routing DataSource");
    return new MultipleRoutingDataSource(creator.getInstance(), creator.getDefaultDataSource());
  }
}
