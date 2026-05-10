package tutorials4j.framework.examples.app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 租户配置
 *
 * @author Yun Jiao
 */
@EnableAsync
@Configuration
@Profile("jpa-database")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.jpa.database"})
@EnableJpaRepositories(basePackages = {"tutorials4j.framework.examples.jpa.database"})
@EntityScan(basePackages = {"tutorials4j.framework.examples.jpa.database"})
public class JpaDatabaseConfig {
}
