package tutorials4j.framework.feature.signin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.feature.signin.autoconfigure.SignInJpaFeatureConfiguration;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SignInJpaFeatureConfiguration.class)
public @interface EnableSignInFeature {}
