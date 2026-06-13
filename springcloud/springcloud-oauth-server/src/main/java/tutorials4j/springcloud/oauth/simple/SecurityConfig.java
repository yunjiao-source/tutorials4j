package tutorials4j.springcloud.oauth.simple;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

/** 安全配置分三条链，而不是一锅炖 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  SecurityFilterChain authorizationServerChain(HttpSecurity http) throws Exception {
    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class).oidc(Customizer.withDefaults());

    http.exceptionHandling(
        ex ->
            ex.defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint("/login"),
                new MediaTypeRequestMatcher(org.springframework.http.MediaType.TEXT_HTML)));

    return http.build();
  }

  @Bean
  @Order(2)
  SecurityFilterChain actuatorChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/actuator/**")
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/actuator/health", "/actuator/prometheus")
                    .permitAll()
                    .anyRequest()
                    .hasRole("OPS"))
        .csrf(csrf -> csrf.disable());
    return http.build();
  }

  @Bean
  @Order(3)
  SecurityFilterChain appChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/", "/api/endpoints", "/login", "/error")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .formLogin(Customizer.withDefaults());
    return http.build();
  }
}
