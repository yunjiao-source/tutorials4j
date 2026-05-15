package tutorials4j.springboot3;

import java.lang.annotation.*;

/**
 * 权限校验注解
 *
 * @author Yun Jiao
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeedPermission {
  // 所需权限编码（如：user:add、user:delete）
  String[] value();
}
