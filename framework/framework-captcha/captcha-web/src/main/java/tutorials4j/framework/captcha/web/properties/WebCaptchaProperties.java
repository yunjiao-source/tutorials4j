package tutorials4j.framework.captcha.web.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;

/**
 * 验证码 Web 模块配置属性。
 *
 * <p>以 {@code tutorials4j.captcha.web} 为前缀绑定外部配置， 包含模块总开关以及验证码认证拦截器的包含/排除路径。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CAPTCHA_WEB)
public class WebCaptchaProperties {
  /** 验证码 Web 模块总开关 */
  private boolean enabled = false;

  /** 验证码认证拦截器配置（包含/排除路径） */
  @NestedConfigurationProperty
  private HandlerInterceptorOptions interceptor = new HandlerInterceptorOptions();
}
