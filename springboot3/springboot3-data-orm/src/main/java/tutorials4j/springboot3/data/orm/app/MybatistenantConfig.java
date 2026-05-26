package tutorials4j.springboot3.data.orm.app;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import tutorials4j.springboot3.common.MybatisCommonConfiguration;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("mybatistenant")
@Configuration
@Import(MybatisCommonConfiguration.class)
@ComponentScan(basePackages = {"tutorials4j.springboot3.data.orm.mybatistenant"})
public class MybatistenantConfig implements CachingConfigurer {}
