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

/** JWK 与密钥轮换设计 */
@Configuration
public class JwkConfig {

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
