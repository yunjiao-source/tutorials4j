package tutorials4j.springcloud.oauth.simple;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWK 配置：基于密钥材料构建 JWK 源，供授权服务器签发 JWT 使用。
 *
 * <p>密钥通过 {@link KeyMaterialLoader} 加载（当前为内存生成，生产环境应从 Vault/KMS 等来源读取）。
 *
 * @author Yun Jiao
 */
@Configuration
public class JwkConfig {

  /**
   * 构建基于 RSA 密钥对的 JWK 源。
   *
   * @param keyMaterialLoader 密钥材料加载器
   * @return JWK 源
   */
  @Bean
  JWKSource<SecurityContext> jwkSource(KeyMaterialLoader keyMaterialLoader) {
    RSAPublicKey publicKey = keyMaterialLoader.loadPublicKey();
    RSAPrivateKey privateKey = keyMaterialLoader.loadPrivateKey();

    RSAKey rsaKey =
        new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(keyMaterialLoader.currentKid())
            .build();

    return new ImmutableJWKSet<>(new JWKSet(rsaKey));
  }
}
