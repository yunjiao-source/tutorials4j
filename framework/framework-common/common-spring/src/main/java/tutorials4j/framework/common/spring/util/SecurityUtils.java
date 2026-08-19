package tutorials4j.framework.common.spring.util;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具接口，提供从 Spring Security 上下文中获取当前登录账号的静态方法。
 *
 * @author Yun Jiao
 */
public interface SecurityUtils {
  /**
   * 获取当前登录账号（Optional 形式）。
   *
   * @return 当前认证用户名，未认证时返回空 Optional
   */
  static Optional<String> getAccountOptional() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .map(Authentication::getName);
  }

  /**
   * 获取当前登录账号。
   *
   * @return 当前认证用户名，未认证时返回 null
   */
  static String getAccount() {
    return getAccountOptional().orElse(null);
  }
}
