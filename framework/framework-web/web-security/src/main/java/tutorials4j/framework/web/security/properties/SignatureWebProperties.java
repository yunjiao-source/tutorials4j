package tutorials4j.framework.web.security.properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;

/**
 * 接口签名校验功能的配置属性类，包含功能开关、拦截器选项、Nonce 缓存前缀与签名密钥映射。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_SIGNATURE)
public class SignatureWebProperties {
  /** 是否启用接口签名校验，默认 false */
  private boolean enabled = false;

  /** 签名校验拦截器选项 */
  @NestedConfigurationProperty
  private HandlerInterceptorOptions interceptor = new HandlerInterceptorOptions();

  /** Nonce 防重放缓存的键前缀 */
  private String nonceRedisKeyPrefix = "signature:nonce:";

  /** 签名密钥映射，key 为 appKey，value 为 appSecret */
  private Map<String, String> keys = new HashMap<>();
}
