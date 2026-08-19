package tutorials4j.framework.common.spring.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;

/**
 * 环境配置属性获取器，提供从 Spring {@link Environment}、{@link ConditionContext} 或 {@link ApplicationContext}
 * 中读取配置属性的便捷静态方法。
 *
 * @author Yun Jiao
 */
public class EnvPropertyFinder {
  /**
   * 从环境信息中获取指定类型的配置属性。
   *
   * @param environment Spring Boot Environment {@link Environment}
   * @param property 配置名称
   * @param targetType 配置类型
   * @return 配置属性值，不存在时返回 null
   * @param <T> 配置类型
   */
  public static <T> T getProperty(Environment environment, String property, Class<T> targetType) {
    return environment.getProperty(property, targetType);
  }

  /**
   * 从环境信息中获取指定类型的配置属性，不存在时返回默认值。
   *
   * @param environment Spring Boot Environment {@link Environment}
   * @param property 配置名称
   * @param targetType 配置类型
   * @param defaultValue 默认值
   * @return 配置属性值，不存在时返回默认值
   * @param <T> 配置类型
   */
  public static <T> T getProperty(
      Environment environment, String property, Class<T> targetType, T defaultValue) {
    return environment.getProperty(property, targetType, defaultValue);
  }

  /**
   * 从条件上下文中获取字符串类型的配置属性。
   *
   * @param conditionContext 条件上下文
   * @param property 配置名称
   * @return 配置属性值，不存在时返回 null
   */
  public static String getStringProperty(ConditionContext conditionContext, String property) {
    return getProperty(conditionContext.getEnvironment(), property, String.class);
  }

  /**
   * 从条件上下文中获取字符串类型的配置属性，不存在时返回默认值。
   *
   * @param conditionContext 条件上下文
   * @param property 配置名称
   * @param defaultValue 默认值
   * @return 配置属性值，不存在时返回默认值
   */
  public static String getStringProperty(
      ConditionContext conditionContext, String property, String defaultValue) {
    return getProperty(conditionContext.getEnvironment(), property, String.class, defaultValue);
  }

  /**
   * 从条件上下文中获取布尔类型的配置属性。
   *
   * @param conditionContext 条件上下文
   * @param property 配置名称
   * @return 配置属性值，不存在时返回 null
   */
  public static Boolean getBoolProperty(ConditionContext conditionContext, String property) {
    return getProperty(conditionContext.getEnvironment(), property, Boolean.class);
  }

  /**
   * 从条件上下文中获取布尔类型的配置属性，不存在时返回默认值。
   *
   * @param conditionContext 条件上下文
   * @param property 配置名称
   * @param defaultValue 默认值
   * @return 配置属性值，不存在时返回默认值
   */
  public static Boolean getBoolProperty(
      ConditionContext conditionContext, String property, Boolean defaultValue) {
    return getProperty(conditionContext.getEnvironment(), property, Boolean.class, defaultValue);
  }

  /**
   * 从应用上下文中获取字符串类型的配置属性。
   *
   * @param applicationContext 应用上下文
   * @param property 配置名称
   * @return 配置属性值，不存在时返回 null
   */
  public static String getStringProperty(ApplicationContext applicationContext, String property) {
    return getProperty(applicationContext.getEnvironment(), property, String.class);
  }

  /**
   * 从应用上下文中获取字符串类型的配置属性，不存在时返回默认值。
   *
   * @param applicationContext 应用上下文
   * @param property 配置名称
   * @param defaultValue 默认值
   * @return 配置属性值，不存在时返回默认值
   */
  public static String getStringProperty(
      ApplicationContext applicationContext, String property, String defaultValue) {
    return getProperty(applicationContext.getEnvironment(), property, String.class, defaultValue);
  }

  /**
   * 从应用上下文中获取布尔类型的配置属性。
   *
   * @param applicationContext 应用上下文
   * @param property 配置名称
   * @return 配置属性值，不存在时返回 null
   */
  public static Boolean getBoolProperty(ApplicationContext applicationContext, String property) {
    return getProperty(applicationContext.getEnvironment(), property, Boolean.class);
  }

  /**
   * 从应用上下文中获取布尔类型的配置属性，不存在时返回默认值。
   *
   * @param applicationContext 应用上下文
   * @param property 配置名称
   * @param defaultValue 默认值
   * @return 配置属性值，不存在时返回默认值
   */
  public static Boolean getBoolProperty(
      ApplicationContext applicationContext, String property, Boolean defaultValue) {
    return getProperty(applicationContext.getEnvironment(), property, Boolean.class, defaultValue);
  }
}
