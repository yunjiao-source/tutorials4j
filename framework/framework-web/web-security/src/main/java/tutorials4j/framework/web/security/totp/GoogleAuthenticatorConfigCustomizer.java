package tutorials4j.framework.web.security.totp;

import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;

/**
 * 函数式接口，用于定制 {@link GoogleAuthenticatorConfig} 的配置项。
 *
 * <p>允许通过 Spring 容器注册多个 {@link GoogleAuthenticatorConfigCustomizer} Bean，
 * 它们将按顺序依次修改配置（例如设置时间步长、窗口大小、密钥长度等）。
 *
 * @author Yun Jiao
 * @see GoogleAuthenticatorConfig
 * @see com.warrenstrange.googleauth.GoogleAuthenticator
 */
@FunctionalInterface
public interface GoogleAuthenticatorConfigCustomizer {

  /**
   * 对传入的配置对象进行自定义修改。
   *
   * @param config 待定制的 {@link GoogleAuthenticatorConfig} 对象
   */
  void customize(GoogleAuthenticatorConfig config);
}
