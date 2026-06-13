package tutorials4j.springcloud.resource.simple;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/** 自定义 claims 与权限映射 */
public class CustomJwtGrantedAuthoritiesConverter
    implements Converter<Jwt, Collection<GrantedAuthority>> {

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
