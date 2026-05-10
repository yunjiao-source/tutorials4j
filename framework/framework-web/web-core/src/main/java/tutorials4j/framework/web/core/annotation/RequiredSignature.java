package tutorials4j.framework.web.core.annotation;

import java.lang.annotation.*;

/**
 * 签名校验注解
 *
 * @author Yun Jiao
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredSignature {
    /**
     * 是否必填签名
     */
    boolean required() default true;

    /**
     * 时间窗口（秒）
     */
    long timeWindow() default 300;

    /**
     * 是否验证 nonce
     */
    boolean checkNonce() default true;
}
