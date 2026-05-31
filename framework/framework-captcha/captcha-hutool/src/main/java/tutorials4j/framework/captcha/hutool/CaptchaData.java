package tutorials4j.framework.captcha.hutool;

import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.captcha.CaptchaCategory;

/**
 * 验证码数据传输对象，封装了验证码的唯一标识、图片字节数组、文本内容及类别。
 *
 * <p>提供将图片字节数组转换为Base64 Data URL的方法，以及转换为Map结构便于接口返回。
 *
 * @author Yun Jiao
 */
@Slf4j
@Getter
@Setter
@ToString
@Accessors(fluent = true, chain = true)
public class CaptchaData implements Serializable {
  /** 验证码唯一标识，通常是uuid字符 */
  private String key;

  /** 验证码图片 */
  private byte[] captchaImage;

  /** 验证码 */
  private String code;

  /** 分类 */
  private CaptchaCategory category;

  /**
   * 转换成图片字符串
   *
   * @return 可能空
   */
  public String captchaImageBase64() {
    if (captchaImage == null) {
      return null;
    }

    String base64 = Base64.getEncoder().encodeToString(captchaImage);
    return "data:image/" + category.getExt() + ";base64," + base64;
  }

  /**
   * 将当前对象转换为Map结构，便于序列化为JSON响应。
   *
   * <p>Map中包含key、category、captchaImage（Base64 Data URL），不包含code字段。
   *
   * @return 包含验证码信息的Map
   */
  public Map<String, Object> toMap() {
    if (log.isDebugEnabled()) {
      log.debug("验证码数据：{}", this);
    }
    Map<String, Object> map = new HashMap<>();
    map.put("key", this.key());
    // map.put("code", this.code());
    map.put("category", this.category().name());
    map.put("captchaImageBase64", captchaImageBase64());
    return map;
  }
}
