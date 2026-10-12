package tutorials4j.toolkit.cache.context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Conditional;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnCaffeineCacheTypeCondition.class)
public @interface ConditionalOnCaffeineCacheType {}
