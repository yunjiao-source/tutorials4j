package tutorials4j.framework.captcha.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * 天意验证码配置属性类，支持通过 Spring Boot 配置文件进行配置。
 *
 * <p>配置前缀为 {@code tutorials4j.captcha.tianai}。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CAPTCHA_TIANAI)
public class TianaiCaptchaProperties {

  /** 公共配置，应用于所有验证码类型 */
  @NestedConfigurationProperty
  private TianaiOptions common = new TianaiOptions("jpeg", "png", false);

  /** 滑动验证码专用配置 */
  @NestedConfigurationProperty private TianaiOptions slider = new TianaiOptions();

  /** 拼图验证码专用配置 */
  @NestedConfigurationProperty private TianaiOptions concat = new TianaiOptions();

  /** 文字点选验证码专用配置 */
  @NestedConfigurationProperty private TianaiOptions wordImageClick = new TianaiOptions();

  /** 旋转验证码专用配置 */
  @NestedConfigurationProperty private TianaiOptions rotate = new TianaiOptions();
}
