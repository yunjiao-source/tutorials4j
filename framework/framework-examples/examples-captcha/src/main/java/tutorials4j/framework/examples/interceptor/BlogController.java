package tutorials4j.framework.examples.interceptor;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.captcha.annotation.CaptchaAuth;
import tutorials4j.framework.captcha.exception.CaptchaErrorCode;
import tutorials4j.framework.common.core.DefaultConsts;

/**
 * 播客
 *
 * @author Yun Jiao
 */
@RestController
public class BlogController {

  @CaptchaAuth
  @PostMapping("post")
  public String post(@RequestBody String content, HttpServletResponse response) {
    String auth = response.getHeader(DefaultConsts.HTTP_HEADER_CAPTCHA_AUTH);
    if (StringUtils.isBlank(auth)) {
      throw CaptchaErrorCode.CAPTCHA_AUTH_FAILURE.throwed();
    }
    return "发布成功";
  }
}
