package tutorials4j.framework.crypto.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法级加密注解，用于标记需要由加密框架自动处理的接口方法。
 *
 * <p>通过 {@code response} 与 {@code request} 两个属性分别控制响应加密与请求解密的开关， 由 Web 层的响应体加密、请求体解密增强器在运行时拦截处理。
 *
 * @author Yun Jiao
 */
@Target(ElementType.METHOD) // 注解仅作用于方法
@Retention(RetentionPolicy.RUNTIME) // 运行时保留，便于AOP拦截获取
public @interface Crypto {
  /** 是否对方法返回值（响应体）进行加密处理，默认开启。 */
  boolean response() default true;

  /** 是否对方法入参（请求体）进行解密处理，默认开启。 */
  boolean request() default true;
}
