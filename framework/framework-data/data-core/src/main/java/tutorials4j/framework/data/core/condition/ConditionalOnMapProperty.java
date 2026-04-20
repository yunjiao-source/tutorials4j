package tutorials4j.framework.data.core.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * Map条件注解
 *
 * @author Yun Jiao
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnCollectionMapCondition.class)
public @interface ConditionalOnMapProperty {

    /**
     * 配置前缀，例如 "my.map"
     */
    String prefix() default "";

    /**
     * 属性名称（与 prefix 拼接形成完整的配置键）
     */
    String name() default "";

    /**
     * name 的别名，与 name 属性互斥，通常只使用其中一个
     */
    String value() default "";

    /**
     * 是否要求 Map 为空。
     * - true：Map 为空时条件匹配
     * - false：Map 非空时条件匹配
     */
    boolean isEmpty() default false;

    /**
     * 当指定配置不存在时，是否应该匹配。
     * - true：配置缺失时条件匹配
     * - false：配置缺失时条件不匹配
     */
    boolean matchIfMissing() default false;
}
