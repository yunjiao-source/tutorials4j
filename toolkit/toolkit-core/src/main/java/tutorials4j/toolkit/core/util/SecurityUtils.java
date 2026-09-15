package tutorials4j.toolkit.core.util;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * TODO
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
