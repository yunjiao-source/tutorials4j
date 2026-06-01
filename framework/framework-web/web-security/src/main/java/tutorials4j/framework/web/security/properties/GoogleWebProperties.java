package tutorials4j.framework.web.security.properties;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;

/**
 * Google Authenticator 的配置属性类。
 *
 * <p>配置前缀为 {@code tutorials4j.web.google-auth}（由 {@link
 * PropertiesConsts#PROPERTY_PREFIX_WEB_GOOGLE} 定义）。
 *
 * <p>示例配置：
 *
 * <pre>
 * tutorials4j:
 *   web.security:
 *     google:
 *       otp-auth-totp-url: "myapp"
 *       credentials:
 *         - username: admin
 *           password: admin123
 *           security-key:
 *         - username: user
 *           password: user123
 *           security-key:
 *       filter:
 *         url-patterns:
 *           - "/secure/*"
 *         order: 1
 *         name: "googleAuthFilter"
 * </pre>
 *
 * @author Yun Jiao
 * @see ServletFilterOptions
 * @see CredentialOptions
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_GOOGLE)
public class GoogleWebProperties {

  /** OTP Auth URI 中的 issuer 参数，同时也是二维码显示的应用名称。 默认值为 {@code tutorials4j}。 */
  private String otpAuthTotpURL = "tutorials4j";

  /** 静态用户凭证列表，可从 YAML 配置文件中读取。 每个凭证包含用户名、密码和可选的秘钥（秘钥可由系统自动生成后回填，但不会持久化到配置文件）。 */
  private List<CredentialOptions> credentials = new ArrayList<>();

  /** TOTP 验证过滤器的配置，包括 URL 匹配模式、执行顺序、过滤器名称等。 */
  @NestedConfigurationProperty
  private ServletFilterOptions filter =
      new ServletFilterOptions(
          new String[] {},
          1,
          "captchaRequestFilter",
          ServletFilterOptions.DEFAULT_DISPATCHER_TYPES);

  /** 单个用户的凭证配置。 */
  @Data
  public static class CredentialOptions {
    /** 用户名 */
    private String username;

    /** TOTP 秘钥（Base32 编码），若为空则首次使用时生成 */
    private String securityKey;
  }
}
