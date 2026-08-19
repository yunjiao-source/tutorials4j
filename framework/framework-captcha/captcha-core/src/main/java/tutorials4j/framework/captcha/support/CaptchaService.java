package tutorials4j.framework.captcha.support;

import java.util.Map;

/**
 * 验证码服务接口。
 *
 * <p>定义验证码的绘制、校验等能力，由各验证码类别对应的服务实现。
 *
 * @author Yun Jiao
 */
public interface CaptchaService {

  /**
   * 绘制并生成验证码。
   *
   * @return 验证码数据，通常包含验证码 ID 与图片内容等
   */
  Map<String, Object> draw();

  /**
   * 校验用户输入的验证码。
   *
   * @param key 验证码标识
   * @param userCode 用户输入的验证码内容
   * @return 校验通过返回 {@code true}，否则返回 {@code false}
   */
  boolean verify(String key, String userCode);

  /**
   * 获取验证码类别。
   *
   * @return 验证码类别
   */
  CaptchaCategory getCategory();
}
