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
  static Optional<String> getAccountOptional() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .map(Authentication::getName);
  }

  static String getAccount() {
    return getAccountOptional().orElse(null);
  }
}
