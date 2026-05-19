package tutorials4j.framework.captcha.hutool.web;

import lombok.Data;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.CaptchaData;

/**
 * 验证码响应
 *
 * @author Yun Jiao
 */
@Data
public class CodeCaptchaReponse {
  /** 验证码唯一标识，通常是uuid字符 */
  private String key;

  /** 验证码图片 */
  private String captchaImageBase64;

  /** 分类 */
  private CaptchaCategory category;

  public static CodeCaptchaReponse of(CaptchaData data) {
    CodeCaptchaReponse reponse = new CodeCaptchaReponse();
    reponse.setCategory(data.category());
    reponse.setKey(data.key());
    reponse.setCaptchaImageBase64(data.captchaImageBase64Url());
    return reponse;
  }
}
