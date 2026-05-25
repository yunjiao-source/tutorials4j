package tutorials4j.springboot3.google;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Google身份验证器工具类
 *
 * @author Yun Jiao
 */
@Service
@RequiredArgsConstructor
public class GoogleAuthUtil {
  private final GoogleAuthenticator googleAuthenticator;

  /**
   * 生成一个新的 Google Authenticator 秘钥
   *
   * @return 秘钥字符串
   */
  public String generateSecretKey() {
    GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
    return key.getKey();
  }

  /**
   * 验证 Google Authenticator 生成的验证码
   *
   * @param secretKey 秘钥
   * @param code 验证码
   * @return 验证结果
   */
  public boolean verifyCode(String secretKey, int code) {
    return googleAuthenticator.authorize(secretKey, code);
  }

  /**
   * 获取用于生成二维码的 URL
   *
   * @param user 用户名
   * @param secretKey 秘钥
   * @return 二维码 URL
   */
  public String getQRBarcodeURL(String user, String secretKey) {
    GoogleAuthenticatorKey key = new GoogleAuthenticatorKey.Builder(secretKey).build();
    return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("yunjiao-java-tutorials", user, key);
  }
}
