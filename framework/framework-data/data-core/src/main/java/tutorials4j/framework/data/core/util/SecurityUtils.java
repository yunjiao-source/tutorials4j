package tutorials4j.framework.data.core.util;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface SecurityUtils {
  static Optional<String> getAccountOpt() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(authentication.getName());
  }

  static String getAccount() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      return null;
    }

    return authentication.getName();
  }
}
