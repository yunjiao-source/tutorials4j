package tutorials4j.springboot3.data.orm.app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("cursor")
@Configuration
@ComponentScan("tutorials4j.springboot3.data.orm.cursor")
@EnableJpaRepositories(basePackages = {"tutorials4j.springboot3.data.orm.cursor"})
@EntityScan(basePackages = {"tutorials4j.springboot3.data.orm.cursor"})
public class CursorConfig {}
