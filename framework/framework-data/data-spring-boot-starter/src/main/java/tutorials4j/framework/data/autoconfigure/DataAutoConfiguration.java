package tutorials4j.framework.data.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.data.core.autoconfigure.DataConfiguration;
import tutorials4j.framework.data.hibernate.autoconfigure.HibernateConfiguration;
import tutorials4j.framework.data.jdbc.autoconfigure.JdbcConfiguration;
import tutorials4j.framework.data.mybatis.autoconfigure.MybatisPlusConfiguration;

/**
 * Data模块自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({DataConfiguration.class, JdbcConfiguration.class
        , HibernateConfiguration.class, MybatisPlusConfiguration.class})
public class DataAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("[DATA] Data Auto Configuration");
    }

}
