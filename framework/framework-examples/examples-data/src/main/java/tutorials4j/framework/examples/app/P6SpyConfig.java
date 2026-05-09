package tutorials4j.framework.examples.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * p6spay配置
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
public class P6SpyConfig {
}
