package tutorials4j.springboot3.web.apicrypto.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口加解密注解 标记在Controller的方法上，控制该接口的请求/响应加解密行为
 *
 * @author Yun Jiao
 */
@Target(ElementType.METHOD) // 注解仅作用于方法
@Retention(RetentionPolicy.RUNTIME) // 运行时保留，便于AOP拦截获取
public @interface Crypto {
  /** 是否对响应体加密，默认开启 */
  boolean response() default true;

  /** 是否对请求体解密，默认开启 */
  boolean request() default true;
}
