package tutorials4j.springcloud.oauth.simple;

import java.time.Duration;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

/**
 * 客户端仓库配置：使用 JDBC 持久化 OAuth2 客户端信息，并在首次启动时初始化示例客户端。
 *
 * <p>预置 web-bff（授权码模式）与 internal-order-service（客户端凭证模式）两个客户端，避免仅依赖内存存储。
 *
 * @author Yun Jiao
 */
@Configuration
public class ClientRepositoryConfig {

  /**
   * 构建基于 JDBC 的客户端仓库，并在客户端不存在时初始化示例客户端数据。
   *
   * @param jdbcTemplate JDBC 模板
   * @param passwordEncoder 密码编码器
   * @return 已注册的客户端仓库
   */
  @Bean
  RegisteredClientRepository registeredClientRepository(
      JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
    JdbcRegisteredClientRepository repository = new JdbcRegisteredClientRepository(jdbcTemplate);

    if (repository.findByClientId("web-bff") == null) {
      RegisteredClient webBff =
          RegisteredClient.withId(UUID.randomUUID().toString())
              .clientId("web-bff")
              .clientSecret(passwordEncoder.encode("change-me-in-vault"))
              .clientName("Web BFF Client")
              .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
              .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
              .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
              .redirectUri("https://bff.example.com/login/oauth2/code/web-bff")
              .postLogoutRedirectUri("https://portal.example.com/logout/callback")
              .scope(OidcScopes.OPENID)
              .scope(OidcScopes.PROFILE)
              .scope("order.read")
              .scope("order.write")
              .clientSettings(
                  ClientSettings.builder()
                      .requireProofKey(true)
                      .requireAuthorizationConsent(true)
                      .build())
              .tokenSettings(
                  TokenSettings.builder()
                      .accessTokenTimeToLive(Duration.ofMinutes(15))
                      .refreshTokenTimeToLive(Duration.ofDays(7))
                      .reuseRefreshTokens(false)
                      .build())
              .build();
      repository.save(webBff);
    }

    if (repository.findByClientId("internal-order-service") == null) {
      RegisteredClient internal =
          RegisteredClient.withId(UUID.randomUUID().toString())
              .clientId("internal-order-service")
              .clientSecret(passwordEncoder.encode("rotate-me"))
              .clientName("Order Internal Client")
              .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
              .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
              .scope("internal.read")
              .scope("inventory.reserve")
              .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
              .tokenSettings(
                  TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(5)).build())
              .build();
      repository.save(internal);
    }

    return repository;
  }
}
