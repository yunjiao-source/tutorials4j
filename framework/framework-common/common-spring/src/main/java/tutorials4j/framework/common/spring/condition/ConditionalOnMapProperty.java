package tutorials4j.framework.common.spring.condition;

import java.lang.annotation.*;
import org.springframework.context.annotation.Conditional;

/**
 * 条件注解：当指定的配置属性存在且对应的 Map（映射）满足空/非空条件时，才创建 Bean。
 *
 * <p>可标注在类或方法上，基于 Spring Boot 的 {@link Conditional} 机制，通过 {@link OnCollectionMapCondition}
 * 实现具体匹配逻辑。
 *
 * <p><b>使用示例：</b>
 *
 * <pre>{@code
 * // 当配置 my.map.settings 存在且非空时，创建 Bean
 * @ConditionalOnMapProperty(prefix = "my.map", name = "settings", isEmpty = false)
 * // 当配置 my.map.settings 缺失或为空 Map 时，创建 Bean
 * @ConditionalOnMapProperty(prefix = "my.map", name = "settings", matchIfMissing = true, isEmpty = true)
 * }</pre>
 *
 * @author Yun Jiao
 * @see OnCollectionMapCondition
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnCollectionMapCondition.class)
public @interface ConditionalOnMapProperty {

  /**
   * 配置前缀，例如 "my.map"。
   *
   * <p>与 {@link #name()} 拼接形成完整的配置键，如 "my.map.settings"。
   *
   * @return 配置前缀，默认为空字符串
   */
  String prefix() default "";

  /**
   * 属性名称，与 {@link #prefix()} 拼接形成完整的配置键。
   *
   * <p>通常与 prefix 配合使用，也可单独使用（此时作为完整键）。
   *
   * @return 属性名称，默认为空字符串
   */
  String name() default "";

  /**
   * name 的别名，与 name 属性互斥，通常只使用其中一个。
   *
   * <p>当 name 未指定而 value 有值时，value 会作为 name 使用；若两者同时指定，优先使用 name。
   *
   * @return 属性名称别名，默认为空字符串
   */
  String value() default "";

  /**
   * 是否要求 Map 为空。
   *
   * <ul>
   *   <li>{@code true}：Map 为空时条件匹配
   *   <li>{@code false}：Map 非空时条件匹配
   * </ul>
   *
   * @return 是否要求空 Map，默认为 false
   */
  boolean isEmpty() default false;

  /**
   * 当指定配置键不存在时，是否应该匹配。
   *
   * <ul>
   *   <li>{@code true}：配置缺失时条件匹配
   *   <li>{@code false}：配置缺失时条件不匹配
   * </ul>
   *
   * @return 缺失时是否匹配，默认为 false
   */
  boolean matchIfMissing() default false;
}
