package tutorials4j.framework.examples.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 租户配置
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("mybatis-table")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.mybatis.table"})
@MapperScan({"tutorials4j.framework.examples.mybatis.table"})
public class MybatisTableConfig {
}
