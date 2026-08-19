package tutorials4j.framework.examples.app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * JPA「表模式」多租户示例配置类。
 *
 * <p>仅在 {@code jpa-table} Profile 下生效：扫描 {@code tutorials4j.framework.examples.jpa.table}
 * 包中的组件，启用该包下的 JPA 实体与 Repository，并开启异步支持。
 *
 * @author Yun Jiao
 */
@EnableAsync
@Configuration
@Profile("jpa-table")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.jpa.table"})
@EnableJpaRepositories(basePackages = {"tutorials4j.framework.examples.jpa.table"})
@EntityScan(basePackages = {"tutorials4j.framework.examples.jpa.table"})
public class JpaTableConfig {}
