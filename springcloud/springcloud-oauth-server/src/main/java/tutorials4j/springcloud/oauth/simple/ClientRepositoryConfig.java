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

/** 客户端仓库不要只用内存 */
@Configuration
public class ClientRepositoryConfig {

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
