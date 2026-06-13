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
 * TODO
 *
 * @author Yun Jiao
 */
@Configuration
public class Config {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JwtDecoder jwtDecoder(KeyMaterialLoader keyMaterialLoader) {
    RSAPublicKey publicKey = keyMaterialLoader.loadPublicKey();
    return NimbusJwtDecoder.withPublicKey(publicKey).build();
  }

  // 新增：提供测试用户
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
