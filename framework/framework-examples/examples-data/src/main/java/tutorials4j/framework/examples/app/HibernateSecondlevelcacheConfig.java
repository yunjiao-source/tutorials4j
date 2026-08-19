package tutorials4j.framework.examples.app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Hibernate 二级缓存示例配置类。
 *
 * <p>在 {@code hibernate-secondlevelcache} profile 下启用，扫描并装配 Hibernate 二级缓存 示例的组件、JPA 仓库与实体。
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("hibernate-secondlevelcache")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.hibernate.secondlevelcache"})
@EnableJpaRepositories(basePackages = {"tutorials4j.framework.examples.hibernate.secondlevelcache"})
@EntityScan(basePackages = {"tutorials4j.framework.examples.hibernate.secondlevelcache"})
public class HibernateSecondlevelcacheConfig {}
