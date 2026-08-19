package tutorials4j.springcloud.resource.simple;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * 自定义 JWT claims 到权限集合的转换器：从 JWT 的 scope 与 roles 声明中提取权限，scope 统一添加 SCOPE_ 前缀，roles 直接作为权限。
 *
 * @author Yun Jiao
 */
public class CustomJwtGrantedAuthoritiesConverter
    implements Converter<Jwt, Collection<GrantedAuthority>> {

  /**
   * 将 JWT 的 scope 与 roles 声明转换为权限集合。
   *
   * @param jwt JWT 令牌
   * @return 权限集合（scope 权限带 SCOPE_ 前缀，roles 权限原样保留）
   */
  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    Set<GrantedAuthority> authorities = new HashSet<>();

    List<String> scopes = jwt.getClaimAsStringList("scope");
    if (scopes != null) {
      scopes.forEach(scope -> authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope)));
    }

    List<String> roles = jwt.getClaimAsStringList("roles");
    if (roles != null) {
      roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
    }

    return authorities;
  }
}
