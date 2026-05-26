package tutorials4j.springboot3.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.common.mybatis"})
@MapperScan({"tutorials4j.springboot3.common.mybatis"})
public class MybatisCommonConfiguration {}
