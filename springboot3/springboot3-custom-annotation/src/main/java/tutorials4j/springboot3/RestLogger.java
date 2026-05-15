package tutorials4j.springboot3;

import java.lang.annotation.*;

/**
 * rest接口日志注解
 *
 * @author Yun Jiao
 */
@Target(ElementType.METHOD) // 仅作用于方法
@Retention(RetentionPolicy.RUNTIME) // 运行时保留，支持反射
@Documented
public @interface RestLogger {
  // 注解参数：描述接口功能，默认空字符串
  String value() default "";

  // 注解参数：是否打印请求参数，默认true
  boolean printParam() default true;

  // 注解参数：是否打印响应结果，默认true
  boolean printResult() default true;
}
