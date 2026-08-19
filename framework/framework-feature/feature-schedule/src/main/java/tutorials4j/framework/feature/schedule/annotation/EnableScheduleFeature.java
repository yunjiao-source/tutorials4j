package tutorials4j.framework.feature.schedule.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.feature.schedule.autoconfigure.ScheduleFeatureConfiguration;

/**
 * 启用调度功能特性的注解。
 *
 * <p>标注在配置类上，通过 {@link Import} 导入调度功能的自动配置。
 *
 * @author Yun Jiao
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ScheduleFeatureConfiguration.class)
public @interface EnableScheduleFeature {}
