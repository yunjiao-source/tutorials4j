package tutorials4j.springcloud.oauth.simple;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

/** 自定义 JWT Claims：只放必要信息 */
@Component
public class JwtClaimCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

  @Override
  public void customize(JwtEncodingContext context) {
    Authentication principal = context.getPrincipal();

    if ("access_token".equals(context.getTokenType().getValue()) && principal != null) {
      Set<String> roles =
          principal.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.toSet());

      context.getClaims().claim("roles", roles);
      context.getClaims().claim("tenant_id", resolveTenantId(principal));
      context.getClaims().claim("login_type", resolveLoginType(context));
    }
  }

  private String resolveTenantId(Authentication principal) {
    return "t_default";
  }

  private String resolveLoginType(JwtEncodingContext context) {
    AuthorizationGrantType grantType = context.getAuthorizationGrantType();
    if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(grantType)) {
      return "service";
    }
    return "user";
  }
}
