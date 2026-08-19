package tutorials4j.framework.captcha.tianai.support;

/**
 * 天意（Tianai）验证码类型枚举。
 *
 * <p>定义了系统支持的验证码交互类型，涵盖拼图、滑动、文字点选和旋转等验证方式。
 *
 * @author Yun Jiao
 */
public enum CaptchaType {
  /** 拼图验证码：拖动滑块将缺口拼图拼合完整 */
  CONCAT,
  /** 滑动验证码：拖动滑块至指定位置 */
  SLIDER,
  /** 文字点选验证码：按提示点选图片中的文字 */
  WORD_IMAGE_CLICK,
  /** 旋转验证码：将图片旋转至正确角度 */
  ROTATE
}
