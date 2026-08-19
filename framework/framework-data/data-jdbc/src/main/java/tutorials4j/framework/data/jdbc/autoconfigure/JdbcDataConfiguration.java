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
 * JDBC 数据源配置类，在存在 DataSource Bean 时装配数据源路由管理器创建器。
 *
 * @author Yun Jiao
 */
@Slf4j
@ConditionalOnBean(DataSource.class)
@Configuration(proxyBeanMethods = false)
public class JdbcDataConfiguration {
  /** 初始化日志输出。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[DATA-JDBC] Data Jdbc Configuration");
  }

  /**
   * 创建数据源路由管理器创建器。
   *
   * @param dataSource 数据源
   * @return 数据源路由管理器创建器
   */
  @Bean
  @ConditionalOnMissingBean
  DataSourceRoutingManagerCreator dataSourceRoutingManagerCreator(DataSource dataSource) {
    log.trace("[DATA-JDBC] Data Source Routing Manager Creator");
    return new DataSourceRoutingManagerCreator(dataSource);
  }
}
