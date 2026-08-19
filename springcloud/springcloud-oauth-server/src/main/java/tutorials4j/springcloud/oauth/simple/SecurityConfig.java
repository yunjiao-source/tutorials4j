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

/**
 * 安全配置：按职责拆分三条安全过滤链。
 *
 * <p>授权服务器链（最高优先级）处理 OAuth2/OIDC 端点，actuator 链保护监控端点（需 OPS 角色）， 应用链处理页面与其余请求。
 *
 * @author Yun Jiao
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  /**
   * 授权服务器安全过滤链，应用 OAuth2 授权服务器默认安全配置并启用 OIDC。
   *
   * @param http HttpSecurity 构建器
   * @return 授权服务器安全过滤链
   * @throws Exception 构建过滤链失败时抛出
   */
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

  /**
   * Actuator 端点安全过滤链：健康检查与 Prometheus 指标公开，其余端点需 OPS 角色。
   *
   * @param http HttpSecurity 构建器
   * @return Actuator 安全过滤链
   * @throws Exception 构建过滤链失败时抛出
   */
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

  /**
   * 应用安全过滤链：公开首页等路径，其余请求需认证，并启用表单登录。
   *
   * @param http HttpSecurity 构建器
   * @return 应用安全过滤链
   * @throws Exception 构建过滤链失败时抛出
   */
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
