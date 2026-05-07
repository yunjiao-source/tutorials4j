package tutorials4j.framework.data.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.data.core.autoconfigure.DataCoreConfiguration;
import tutorials4j.framework.data.hibernate.autoconfigure.DataHibernateConfiguration;
import tutorials4j.framework.data.jdbc.autoconfigure.DataJdbcConfiguration;
import tutorials4j.framework.data.mybatis.autoconfigure.DataMybatisPlusConfiguration;

/**
 * Data模块自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({DataCoreConfiguration.class, DataJdbcConfiguration.class
        , DataHibernateConfiguration.class, DataMybatisPlusConfiguration.class})
public class DataAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Data |- Data Auto Configuration");
    }

}
