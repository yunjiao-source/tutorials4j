package tutorials4j.springcloud.oauth.simple;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

/**
 * 自定义 JWT Claims 的定制器，仅在 Access Token 中加入必要信息。
 *
 * <p>向 Token 声明中加入角色（roles）、租户 ID（tenant_id）与登录类型（login_type）等自定义声明。
 *
 * @author Yun Jiao
 */
@Component
public class JwtClaimCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

  /**
   * 定制 JWT 编码上下文，向 Access Token 中加入自定义声明。
   *
   * @param context JWT 编码上下文
   */
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
