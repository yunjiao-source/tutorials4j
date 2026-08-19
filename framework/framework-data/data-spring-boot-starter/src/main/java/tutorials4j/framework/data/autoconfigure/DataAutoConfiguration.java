package tutorials4j.framework.data.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.data.core.autoconfigure.DataConfiguration;
import tutorials4j.framework.data.hibernate.autoconfigure.HibernateDataConfiguration;
import tutorials4j.framework.data.jdbc.autoconfigure.JdbcDataConfiguration;
import tutorials4j.framework.data.mybatis.autoconfigure.MybatisPlusDataConfiguration;

/**
 * Data 模块自动配置入口。
 *
 * <p>通过 {@link Import} 统一导入 Data 核心配置、JDBC 配置、Hibernate 配置与 MyBatis Plus
 * 配置，供框架使用者一键启用数据访问模块的自动装配能力。
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  DataConfiguration.class,
  JdbcDataConfiguration.class,
  HibernateDataConfiguration.class,
  MybatisPlusDataConfiguration.class
})
public class DataAutoConfiguration {
  /** 启动后打印 Data 模块自动配置的初始化日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[DATA] Data Auto Configuration");
  }
}
