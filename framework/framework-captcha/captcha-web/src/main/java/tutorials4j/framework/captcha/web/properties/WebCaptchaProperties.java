package tutorials4j.framework.captcha.web.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;

/**
 * 验证码属性配置
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CAPTCHA_WEB)
public class WebCaptchaProperties {
  private boolean enabled = false;

  @NestedConfigurationProperty
  private HandlerInterceptorOptions interceptor = new HandlerInterceptorOptions();
}
