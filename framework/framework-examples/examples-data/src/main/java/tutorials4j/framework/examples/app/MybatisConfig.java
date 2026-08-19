package tutorials4j.framework.examples.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * MyBatis 示例配置类。
 *
 * <p>在 {@code mybatis} profile 下启用，扫描并装配 MyBatis 示例的组件与 Mapper。
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("mybatis")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.mybatis"})
@MapperScan({"tutorials4j.framework.examples.mybatis"})
public class MybatisConfig {}
