package tutorials4j.springboot3.common;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Configuration
@ComponentScan("tutorials4j.springboot3.common.jpa")
@EnableJpaRepositories(basePackages = {"tutorials4j.springboot3.common.jpa"})
@EntityScan(basePackages = {"tutorials4j.springboot3.common.jpa"})
public class JpaCommonConfiguration {}
