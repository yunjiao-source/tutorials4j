package tutorials4j.framework.examples.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * MyBatis「表模式」多租户示例配置类。
 *
 * <p>仅在 {@code mybatis-table} Profile 下生效：扫描 {@code tutorials4j.framework.examples.mybatis.table}
 * 包中的组件，并注册该包下的 MyBatis Mapper，同时开启异步支持。
 *
 * @author Yun Jiao
 */
@EnableAsync
@Configuration
@Profile("mybatis-table")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.mybatis.table"})
@MapperScan({"tutorials4j.framework.examples.mybatis.table"})
public class MybatisTableConfig {}
