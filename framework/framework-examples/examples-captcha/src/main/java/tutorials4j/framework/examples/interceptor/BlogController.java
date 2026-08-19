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
 * 博客发布示例控制器。
 *
 * <p>演示通过 {@link CaptchaAuth} 注解进行验证码校验后发布内容，若响应头中缺少 验证码认证信息则抛出验证码认证失败异常。
 *
 * @author Yun Jiao
 */
@RestController
public class BlogController {

  /**
   * 发布博客内容（需通过验证码校验）。
   *
   * @param content 发布内容
   * @param response HTTP 响应，用于读取验证码认证头
   * @return 发布成功提示
   */
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
