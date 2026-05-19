package tutorials4j.framework.captcha.hutool.web;

import lombok.Data;
import tutorials4j.framework.captcha.CaptchaCategory;

/**
 * 验证码校验对象
 *
 * @author Yun Jiao
 */
@Data
public class CodeCaptchaRequest {
  /** 验证码唯一标识，通常是uuid字符 */
  private String key;

  /** 验证码 */
  private String code;

  /** 分类 */
  private CaptchaCategory category;
}
