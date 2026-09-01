package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tutorials4j.framework.feature.oss.annotation.EnableFileStorageSpringFeature;

/**
 * @author Yun Jiao
 */
@EnableFileStorageSpringFeature
@Configuration
@Profile("oss")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.oss"})
public class OssConfig {}
