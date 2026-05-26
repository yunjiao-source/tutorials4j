package tutorials4j.framework.captcha.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;

/**
 * 验证码属性配置
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CAPTCHA)
public class CaptchaProperties {
  /** 验证码过滤器 */
  @NestedConfigurationProperty
  private ServletFilterOptions filter =
      new ServletFilterOptions(
          new String[] {},
          1,
          "captchaRequestFilter",
          ServletFilterOptions.DEFAULT_DISPATCHER_TYPES);
}
