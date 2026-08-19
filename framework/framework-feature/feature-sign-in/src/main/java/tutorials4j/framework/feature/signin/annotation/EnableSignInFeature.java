package tutorials4j.framework.feature.signin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.feature.signin.autoconfigure.SignInJpaFeatureConfiguration;

/**
 * 启用签到功能特性的注解。
 *
 * <p>标注在配置类上，通过 {@link Import} 导入签到功能的 JPA 自动配置。
 *
 * @author Yun Jiao
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SignInJpaFeatureConfiguration.class)
public @interface EnableSignInFeature {}
