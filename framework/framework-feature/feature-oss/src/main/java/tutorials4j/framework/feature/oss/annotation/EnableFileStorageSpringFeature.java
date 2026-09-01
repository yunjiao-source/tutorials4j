package tutorials4j.framework.feature.oss.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.feature.oss.autoconfigure.FileStorageSpringFeatureConfiguration;

/**
 * 启用文件存储Spring特性的注解。
 *
 * <p>将该注解标注在Spring Boot启动类或配置类上，即可导入{@link FileStorageSpringFeatureConfiguration}，
 * 从而激活文件存储（OSS）相关的Bean和功能。
 *
 * @author Yun Jiao
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FileStorageSpringFeatureConfiguration.class)
public @interface EnableFileStorageSpringFeature {}
