package tutorials4j.springcloud.resource.simple;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 资源服务器安全配置：配置 JWT 资源服务器的安全过滤链与 JWT 权限转换器，并启用方法级安全控制。
 *
 * @author Yun Jiao
 */
@Configuration
@EnableMethodSecurity
public class ResourceServerConfig {

  /**
   * 构建资源服务器安全过滤链：放行健康检查，/api/orders/** 需要 SCOPE_order.read 权限，其余请求需认证。
   *
   * @param http HttpSecurity 安全构建器
   * @return 安全过滤链
   * @throws Exception 构建安全过滤链时可能抛出的异常
   */
  @Bean
  SecurityFilterChain resourceServerSecurity(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/actuator/health")
                    .permitAll()
                    .requestMatchers("/api/orders/**")
                    .hasAuthority("SCOPE_order.read")
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .csrf(csrf -> csrf.disable());

    return http.build();
  }

  /**
   * 创建 JWT 认证转换器，使用自定义的权限转换器解析 JWT 中的 scope 与 roles 声明。
   *
   * @return JWT 认证转换器
   */
  @Bean
  Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter());
    return converter;
  }
}
