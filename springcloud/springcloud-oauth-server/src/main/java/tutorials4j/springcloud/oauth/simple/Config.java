package tutorials4j.springcloud.oauth.simple;

import java.security.interfaces.RSAPublicKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * OAuth2 授权服务器的基础配置。
 *
 * <p>提供密码编码器、基于公钥的 JWT 解码器，以及用于本地测试的内存用户。
 *
 * @author Yun Jiao
 */
@Configuration
public class Config {
  /**
   * 提供密码编码器（BCrypt）。
   *
   * @return 密码编码器
   */
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 提供基于公钥的 JWT 解码器，用于校验授权服务器签发的令牌。
   *
   * @param keyMaterialLoader 密钥材料加载器
   * @return JWT 解码器
   */
  @Bean
  public JwtDecoder jwtDecoder(KeyMaterialLoader keyMaterialLoader) {
    RSAPublicKey publicKey = keyMaterialLoader.loadPublicKey();
    return NimbusJwtDecoder.withPublicKey(publicKey).build();
  }

  // 新增：提供测试用户
  /**
   * 提供内存用户详情服务，用于本地测试登录。
   *
   * @param passwordEncoder 密码编码器
   * @return 用户详情服务
   */
  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails user =
        User.builder()
            .username("user1")
            .password(passwordEncoder.encode("123456"))
            .roles("USER", "OPS")
            .build();
    return new InMemoryUserDetailsManager(user);
  }
}
