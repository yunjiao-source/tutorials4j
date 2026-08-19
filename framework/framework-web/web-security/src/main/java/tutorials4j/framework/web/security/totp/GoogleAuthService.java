package tutorials4j.framework.web.security.totp;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;

/**
 * Google Authenticator 业务逻辑服务，提供密钥生成、TOTP 校验、OTP Auth URI 生成等功能。
 *
 * <p>该类封装了底层 {@link GoogleAuthenticator} 的操作，简化了调用方式。所需的 {@link GoogleAuthenticator} 实例与 OTP Auth
 * URI 的 issuer 名称通过构造器注入。
 *
 * @author Yun Jiao
 * @see GoogleAuthenticator
 * @see GoogleAuthenticatorQRGenerator
 */
@RequiredArgsConstructor
public class GoogleAuthService {

  private final GoogleAuthenticator googleAuthenticator;
  private final String otpAuthTotpURL;

  /**
   * 生成一个新的密钥（不关联用户名）。
   *
   * @return 包含新生成密钥信息的 {@link GoogleAuthenticatorKey} 对象
   */
  public GoogleAuthenticatorKey generateSecretKey() {
    return googleAuthenticator.createCredentials();
  }

  /**
   * 为指定用户生成一个新的密钥，并将其与用户名关联（通常存储于 CredentialRepository 中）。
   *
   * @param userName 用户名
   * @return 包含新生成密钥信息的 {@link GoogleAuthenticatorKey} 对象
   */
  public GoogleAuthenticatorKey generateSecretKey(String userName) {
    return googleAuthenticator.createCredentials(userName);
  }

  /**
   * 根据用户名及其输入的 TOTP 验证码进行校验（使用该用户名关联的密钥）。
   *
   * @param userName 用户名
   * @param code TOTP 验证码（6位数字）
   * @return 验证通过返回 {@code true}，否则返回 {@code false}
   */
  public boolean verifyByUserName(String userName, int code) {
    return googleAuthenticator.authorizeUser(userName, code);
  }

  /**
   * 根据密钥直接校验 TOTP 验证码（不依赖用户名）。
   *
   * @param secretKey 密钥
   * @param code TOTP 验证码
   * @return 验证通过返回 {@code true}，否则返回 {@code false}
   */
  public boolean verifyBySecretKey(String secretKey, int code) {
    return googleAuthenticator.authorize(secretKey, code);
  }

  /**
   * 生成用于绑定 Google Authenticator 应用的 OTP Auth URI（通常用于生成二维码）。
   *
   * <p>内部会先为该用户生成一个新的密钥，再基于 {@code otpAuthTotpURL}（issuer）与用户名拼接 URI。
   *
   * @param user 用户名（同时作为 OTP Auth URI 中的账号标识，并用于关联密钥）
   * @return OTP Auth URI，例如 {@code otpauth://totp/tutorials4j:user?secret=xxx&issuer=tutorials4j}
   */
  public String getQRBarcodeURL(String user) {
    GoogleAuthenticatorKey key = generateSecretKey(user);
    return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(otpAuthTotpURL, user, key);
  }
}
