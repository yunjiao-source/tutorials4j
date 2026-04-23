package tutorials4j.framework.common.core.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;

/**
 * 环境配置属性获取器
 *
 * @author Yun Jiao
 */
public class EnvPropertyFinder {
    /**
     * 从环境信息中获取配置信息
     * @param environment Spring Boot Environment {@link Environment}
     * @param property 配置名称
     * @param targetType 配置類型
     * @return 配置属性值
     * @param <T> 配置類型
     */
    public static  <T> T getProperty(Environment environment, String property, Class<T> targetType) {
        return environment.getProperty(property, targetType);
    }

    /**
     * 从环境信息中获取配置信息
     * @param environment Spring Boot Environment {@link Environment}
     * @param property 配置名称
     * @param targetType 配置類型
     * @param defaultValue 默认值
     * @return 配置属性值
     * @param <T> 配置類型
     */
    public static  <T> T getProperty(Environment environment, String property, Class<T> targetType, T defaultValue) {
        return environment.getProperty(property, targetType, defaultValue);
    }

    public static  String getStringProperty(ConditionContext conditionContext, String property) {
        return getProperty(conditionContext.getEnvironment(), property, String.class);
    }

    public static  String getStringProperty(ConditionContext conditionContext, String property, String defaultValue) {
        return getProperty(conditionContext.getEnvironment(), property, String.class, defaultValue);
    }

    public static  Boolean getBoolProperty(ConditionContext conditionContext, String property) {
        return getProperty(conditionContext.getEnvironment(), property, Boolean.class);
    }

    public static  Boolean getBoolProperty(ConditionContext conditionContext, String property, Boolean defaultValue) {
        return getProperty(conditionContext.getEnvironment(), property, Boolean.class, defaultValue);
    }

    public static  String getStringProperty(ApplicationContext applicationContext, String property) {
        return getProperty(applicationContext.getEnvironment(), property, String.class);
    }

    public static  String getStringProperty(ApplicationContext applicationContext, String property, String defaultValue) {
        return getProperty(applicationContext.getEnvironment(), property, String.class, defaultValue);
    }

    public static  Boolean getBoolProperty(ApplicationContext applicationContext, String property) {
        return getProperty(applicationContext.getEnvironment(), property, Boolean.class);
    }

    public static  Boolean getBoolProperty(ApplicationContext applicationContext, String property, Boolean defaultValue) {
        return getProperty(applicationContext.getEnvironment(), property, Boolean.class, defaultValue);
    }
}
