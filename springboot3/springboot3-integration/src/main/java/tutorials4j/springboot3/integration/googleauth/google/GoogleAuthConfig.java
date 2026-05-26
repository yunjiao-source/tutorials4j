package tutorials4j.springboot3.integration.googleauth.google;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.ICredentialRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Configuration
public class GoogleAuthConfig {
  @Bean
  public GoogleAuthenticator googleAuthenticator(ICredentialRepository repository) {
    // 创建配置
    GoogleAuthenticatorConfig config =
        new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().build();

    GoogleAuthenticator authenticator = new GoogleAuthenticator(config);
    // 关键：设置我们自定义的凭证仓库
    authenticator.setCredentialRepository(repository);
    return authenticator;
  }
}
