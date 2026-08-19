package tutorials4j.framework.web.security.properties;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;

/**
 * Totp Authenticator 的配置属性类。
 *
 * @author Yun Jiao
 * @see ServletFilterOptions
 * @see CredentialOptions
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_TOTP)
public class TotpWebProperties {
  /** 是否启用 TOTP 认证，默认 false */
  private boolean enabled = false;

  /** OTP Auth URI 中的 issuer 参数，同时也是二维码显示的应用名称。 默认值为 {@code tutorials4j}。 */
  private String otpAuthTotpURL = "tutorials4j";

  /** TOTP 认证器算法选项 */
  private AuthenticatorOptions authenticator = new AuthenticatorOptions();

  /** 静态用户凭证列表，可从 YAML 配置文件中读取。 每个凭证包含用户名、密码和可选的秘钥（秘钥可由系统自动生成后回填，但不会持久化到配置文件）。 */
  private List<CredentialOptions> credentials = new ArrayList<>();

  /** TOTP 校验拦截器选项 */
  @NestedConfigurationProperty
  private HandlerInterceptorOptions interceptor = new HandlerInterceptorOptions();

  /** 单个用户的凭证配置。 */
  @Data
  public static class CredentialOptions {
    /** 用户名 */
    private String username;

    /** TOTP 秘钥（Base32 编码），若为空则首次使用时生成 */
    private String securityKey;
  }

  /** TOTP 认证器的算法选项配置。 */
  @Data
  public static class AuthenticatorOptions {
    /** 时间步长（毫秒），默认 30000 毫秒（30 秒） */
    private long timeStepSizeInMillis = 30000;

    /** 允许的时间窗口大小 */
    private int windowSize = 3;

    /** 验证码位数 */
    private int codeDigits = 6;

    /** 备用一次性验证码（scratch codes）数量 */
    private int numberOfScratchCodes = 5;

    /** 生成的密钥比特数 */
    private int secretBits = 160;
  }
}
