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
  TIANAI_CONCAT("png", "滑动还原验证码"),
  TIANAI_ROTATE("png", "旋转验证码"),
  TIANAI_WORD_IMAGE_CLICK("png", "点选验证码"),
  TIANAI_SLIDER("png", "滑块验证码"),

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
