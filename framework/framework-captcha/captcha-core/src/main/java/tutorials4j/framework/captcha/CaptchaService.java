package tutorials4j.framework.captcha;

import java.util.Map;

/**
 * 验证码服务接口
 *
 * @author Yun Jiao
 */
public interface CaptchaService {

  Map<String, Object> draw();

  boolean verify(String key, String userCode);

  CaptchaCategory getCategory();
}
