package tutorials4j.framework.examples.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * mybatis配置
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("mybatis")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.mybatis"})
@MapperScan({"tutorials4j.framework.examples.mybatis"})
public class MybatisConfig {}
