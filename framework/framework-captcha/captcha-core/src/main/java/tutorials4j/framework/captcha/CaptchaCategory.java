package tutorials4j.framework.captcha;

import lombok.Getter;
import lombok.ToString;

/**
 * 验证码类别
 *
 * @author Yun Jiao
 */
@Getter
@ToString
public enum CaptchaCategory {
  /** 线段干扰验证码 */
  HUTOOL_LINE("png", "线段干扰验证码"),

  /** 圆圈干扰验证码 */
  HUTOOL_CIRCLE("png", "圆圈干扰验证码"),

  /** 扭曲干扰验证码 */
  HUTOOL_SHEAR("png", "扭曲干扰验证码"),

  /** GIF验证码 */
  HUTOOL_GIF("gif", "GIF验证码");

  /** 描述 */
  private final String description;

  /** 扩展名 */
  private final String ext;

  CaptchaCategory(String ext, String description) {
    this.ext = ext;
    this.description = description;
  }
}
