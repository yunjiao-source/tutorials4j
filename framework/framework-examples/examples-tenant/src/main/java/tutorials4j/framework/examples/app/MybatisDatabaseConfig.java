package tutorials4j.framework.examples.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 租户配置
 *
 * @author Yun Jiao
 */
@EnableAsync
@Configuration
@Profile("mybatis-database")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.mybatis.database"})
@MapperScan({"tutorials4j.framework.examples.mybatis.database"})
public class MybatisDatabaseConfig {}
