package tutorials4j.framework.captcha.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CAPTCHA_TIANAI)
public class TianaiCaptchaProperties {
  @NestedConfigurationProperty
  private TianaiOptions common = new TianaiOptions("jpeg", "png", false);

  @NestedConfigurationProperty private TianaiOptions slider = new TianaiOptions();

  @NestedConfigurationProperty private TianaiOptions concat = new TianaiOptions();

  @NestedConfigurationProperty private TianaiOptions wordImageClick = new TianaiOptions();

  @NestedConfigurationProperty private TianaiOptions rotate = new TianaiOptions();
}
