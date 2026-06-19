package tutorials4j.framework.examples.app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("hibernate-secondlevelcache")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.hibernate.secondlevelcache"})
@EnableJpaRepositories(basePackages = {"tutorials4j.framework.examples.hibernate.secondlevelcache"})
@EntityScan(basePackages = {"tutorials4j.framework.examples.hibernate.secondlevelcache"})
public class HibernateSecondlevelcacheConfig {}
