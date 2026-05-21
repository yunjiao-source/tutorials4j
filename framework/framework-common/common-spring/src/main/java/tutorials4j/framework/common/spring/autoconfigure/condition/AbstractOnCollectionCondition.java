package tutorials4j.framework.common.spring.autoconfigure.condition;

import lombok.Builder;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * Spring Boot 条件注解的抽象基类，用于处理基于集合类型（List/Map）等配置属性的条件匹配。
 *
 * <p>子类需要实现 {@link #makeDecision(String, ConditionContext, AnnotationAttributes)} 方法，
 * 并提供具体的集合绑定逻辑（如绑定为 List 或 Map）。
 *
 * @author Yun Jiao
 * @see ConditionalOnListProperty
 * @see ConditionalOnMapProperty
 */
public abstract class AbstractOnCollectionCondition extends SpringBootCondition {

  /**
   * 根据指定的配置键和上下文，做出条件匹配决策。
   *
   * @param fullKey 完整的配置键（prefix + name）
   * @param context 条件上下文，可访问环境、资源加载器等
   * @param attributes 注解属性（如 isEmpty, matchIfMissing）
   * @return 决策结果，包含配置是否存在、绑定的集合是否为空、条件是否匹配等信息
   */
  protected abstract Decision makeDecision(
      String fullKey, ConditionContext context, AnnotationAttributes attributes);

  /**
   * 获取当前条件对应的注解类。
   *
   * @return 注解类，如 {@link ConditionalOnListProperty} 或 {@link ConditionalOnMapProperty}
   */
  protected abstract Class<?> getAnnotationClass();

  /**
   * 核心匹配逻辑，由 Spring Boot 调用。
   *
   * <p>提取注解属性，构建完整配置键，并委托给 {@link #makeDecision} 进行判断。
   *
   * @param context 条件上下文
   * @param metadata 被标注元素的元数据
   * @return 条件匹配结果及描述信息
   */
  @Override
  public ConditionOutcome getMatchOutcome(
      ConditionContext context, AnnotatedTypeMetadata metadata) {
    AnnotationAttributes attributes =
        AnnotationAttributes.fromMap(
            metadata.getAnnotationAttributes(getAnnotationClass().getName()));
    if (attributes == null) {
      return ConditionOutcome.noMatch("@ConditionalOnMapProperty is not present");
    }

    String prefix = attributes.getString("prefix");
    String name = attributes.getString("name");
    String value = attributes.getString("value");
    boolean isEmpty = attributes.getBoolean("isEmpty");
    boolean matchIfMissing = attributes.getBoolean("matchIfMissing");

    // value 作为 name 的别名
    if (StringUtils.hasText(value) && !StringUtils.hasText(name)) {
      name = value;
    }

    // 构建完整的配置键
    String fullKey = buildFullKey(prefix, name);
    if (!StringUtils.hasText(fullKey)) {
      return ConditionOutcome.noMatch("No prefix or name specified");
    }

    Decision decision = makeDecision(fullKey, context, attributes);

    String message =
        String.format(
            "Map property '%s' [%s], isEmpty=%s, matchIfMissing=%s -> %s",
            fullKey,
            !decision.notFound ? (decision.boundIsEmpty ? "empty" : "not empty") : "missing",
            isEmpty,
            matchIfMissing,
            decision.conditionMatches);

    return new ConditionOutcome(decision.conditionMatches, message);
  }

  /**
   * 拼接 prefix 和 name 形成完整配置键。
   *
   * @param prefix 配置前缀，可为空
   * @param name 属性名，可为空
   * @return 完整键名，如 "prefix.name"；若两者均为空则返回空字符串
   */
  private String buildFullKey(String prefix, String name) {
    if (!StringUtils.hasText(prefix)) {
      return StringUtils.hasText(name) ? name : "";
    }
    if (!StringUtils.hasText(name)) {
      return prefix;
    }
    return prefix + "." + name;
  }

  /**
   * 条件匹配决策的不可变记录。
   *
   * @param notFound 配置键是否完全缺失（未绑定任何值）
   * @param boundIsEmpty 配置存在时，绑定的集合是否为空
   * @param conditionMatches 最终条件是否匹配
   */
  @Builder
  public record Decision(boolean notFound, boolean boundIsEmpty, boolean conditionMatches) {}
}
