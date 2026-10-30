package tutorials4j.toolkit.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import tutorials4j.toolkit.core.constant.DefaultConsts;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public interface SecurityUtils {

  static Optional<Authentication> getAuthenticationOptional() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
  }

  static Optional<String> getAccountOptional() {
    return getAuthenticationOptional().map(Authentication::getName);
  }

  static String getAccount() {
    return getAccountOptional().orElse(null);
  }

  static boolean isAuthenticated() {
    return getAuthenticationOptional()
        .filter(Authentication::isAuthenticated)
        .filter(a -> !(a instanceof AnonymousAuthenticationToken))
        .isPresent();
  }

  static Collection<? extends GrantedAuthority> getAuthorities() {
    return getAuthenticationOptional()
        .map(Authentication::getAuthorities)
        .orElseGet(Collections::emptyList);
  }

  static Set<String> getAuthorityNames() {
    return getAuthorities().stream()
        .filter(Objects::nonNull)
        .map(GrantedAuthority::getAuthority)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  static boolean hasAuthority(String authority) {
    return authority != null && getAuthorityNames().contains(authority);
  }

  static boolean hasAnyAuthority(String... authorities) {
    if (authorities == null || authorities.length == 0) return false;
    Set<String> names = getAuthorityNames();
    for (String a : authorities) {
      if (a != null && names.contains(a)) return true;
    }
    return false;
  }

  static boolean hasAllAuthorities(String... authorities) {
    if (authorities == null || authorities.length == 0) return false;
    Set<String> names = getAuthorityNames();
    for (String a : authorities) {
      if (a == null || !names.contains(a)) return false;
    }
    return true;
  }

  static boolean hasRole(String role) {
    if (role == null || role.isBlank()) return false;
    return hasAuthority(
        role.startsWith(DefaultConsts.ROLE_PREFIX) ? role : DefaultConsts.ROLE_PREFIX + role);
  }

  static boolean hasAnyRole(String... roles) {
    if (roles == null) return false;
    for (String r : roles) {
      if (hasRole(r)) return true;
    }
    return false;
  }

  static boolean isAdmin() {
    return hasRole(DefaultConsts.ADMIN_NAME);
  }

  static boolean isNotAdmin() {
    return !isAdmin();
  }
}
