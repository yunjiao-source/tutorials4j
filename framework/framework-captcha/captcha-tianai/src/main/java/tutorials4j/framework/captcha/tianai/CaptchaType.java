package tutorials4j.framework.captcha.tianai;

/**
 * 天意验证码类型枚举。
 *
 * @author Yun Jiao
 */
public enum CaptchaType {
  /** 拼图验证码 */
  CONCAT,
  /** 滑动验证码 */
  SLIDER,
  /** 文字点选验证码 */
  WORD_IMAGE_CLICK,
  /** 旋转验证码 */
  ROTATE
}
