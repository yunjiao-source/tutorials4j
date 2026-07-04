package tutorials4j.framework.data.jdbc.autoconfigure;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.data.jdbc.routing.DataSourceRoutingManagerCreator;

/**
 * Jdbc配置
 *
 * @author Yun Jiao
 */
@Slf4j
@ConditionalOnBean(DataSource.class)
@Configuration(proxyBeanMethods = false)
public class JdbcDataConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[DATA-JDBC] Data Jdbc Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  DataSourceRoutingManagerCreator dataSourceRoutingManagerCreator(DataSource dataSource) {
    log.trace("[DATA-JDBC] Data Source Routing Manager Creator");
    return new DataSourceRoutingManagerCreator(dataSource);
  }
}
