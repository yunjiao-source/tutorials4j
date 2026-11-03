package tutorials4j.toolkit.data.hibernate.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class JpaBasePackagesCondition implements Condition {

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    Environment env = context.getEnvironment();
    BindResult<String[]> entityBasePackages =
        Binder.get(env)
            .bind(
                PropertyConsts.PROPERTY_TOOLKIT_DATA_JPA_ENTITY_BASE_PACKAGES,
                Bindable.of(String[].class));
    if (!entityBasePackages.isBound()) {
      log.warn(
          "框架 JPA 自动配置将不生效, 原因：属性'{}' 未配置",
          PropertyConsts.PROPERTY_TOOLKIT_DATA_JPA_ENTITY_BASE_PACKAGES);
      return false;
    }
    BindResult<String[]> repositoryBasePackages =
        Binder.get(env)
            .bind(
                PropertyConsts.PROPERTY_TOOLKIT_DATA_JPA_REPOSITORY_BASE_PACKAGES,
                Bindable.of(String[].class));
    if (!entityBasePackages.isBound()) {
      log.warn(
          "框架 JPA 自动配置将不生效, 原因：属性'{}' 未配置",
          PropertyConsts.PROPERTY_TOOLKIT_DATA_JPA_REPOSITORY_BASE_PACKAGES);
      return false;
    }

    return true;
  }
}
