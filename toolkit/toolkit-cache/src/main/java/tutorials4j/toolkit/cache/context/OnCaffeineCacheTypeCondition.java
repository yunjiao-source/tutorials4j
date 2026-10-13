package tutorials4j.toolkit.cache.context;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import tutorials4j.toolkit.cache.CacheType;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class OnCaffeineCacheTypeCondition implements Condition {
  private final CacheType expected = CacheType.caffeine;

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    Environment env = context.getEnvironment();
    CacheType type =
        Binder.get(env)
            .bind(PropertyConsts.PROPERTY_TOOLKIT_CACHE + ".type", CacheType.class)
            .orElse(CacheType.redis); // 默认值
    return type == expected;
  }
}
