package tutorials4j.framework.examples.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * P6Spy 示例配置类。
 *
 * <p>在 {@code p6spy} profile 下启用，同时装配 MyBatis 与 JPA 示例的组件、Mapper、仓库与实体， 用于演示 P6Spy 对 SQL 的打印与监控。
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("p6spy")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.mybatis"})
@MapperScan({"tutorials4j.framework.examples.mybatis"})
@ComponentScan(basePackages = {"tutorials4j.framework.examples.jpa"})
@EnableJpaRepositories(basePackages = {"tutorials4j.framework.examples.jpa"})
@EntityScan(basePackages = {"tutorials4j.framework.examples.jpa"})
public class P6SpyConfig {}
