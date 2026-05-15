package tutorials4j.framework.examples.app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 租户配置
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("jpa-table")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.jpa.table"})
@EnableJpaRepositories(basePackages = {"tutorials4j.framework.examples.jpa.table"})
@EntityScan(basePackages = {"tutorials4j.framework.examples.jpa.table"})
public class JpaTableConfig {}
