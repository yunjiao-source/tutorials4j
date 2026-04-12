package tutorials4j.framework.autoconfigure.servlet;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * {@link CachedBodyConfig} 配置开启
 *
 * @author Yun Jiao
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CachedBodyConfig.class)
public @interface EnableCachedBody {
}
