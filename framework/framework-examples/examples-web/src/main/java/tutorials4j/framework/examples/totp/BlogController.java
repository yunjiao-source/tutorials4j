package tutorials4j.framework.examples.totp;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.web.core.annotation.TotpAuth;
import tutorials4j.framework.web.core.exception.WebErrorCode;

/**
 * 博客发布示例控制器，演示 TOTP 双因素认证保护下的接口调用。
 *
 * <p>该接口通过 {@link TotpAuth} 注解要求请求携带有效的 TOTP 认证头， 认证失败时抛出 {@link
 * WebErrorCode#WEB_TOTP_AUTH_FAILURE} 对应的异常。
 *
 * @author Yun Jiao
 */
@RestController
public class BlogController {

  /**
   * 发布博客内容，要求请求已通过 TOTP 认证。
   *
   * @param content 博客内容
   * @param response 响应对象，用于获取 TOTP 认证头
   * @return 发布成功提示信息
   */
  @TotpAuth
  @PostMapping("post")
  public String post(@RequestBody String content, HttpServletResponse response) {
    String auth = response.getHeader(DefaultConsts.HTTP_HEADER_TOTP_AUTH);
    if (StringUtils.isBlank(auth)) {
      throw WebErrorCode.WEB_TOTP_AUTH_FAILURE.throwed();
    }
    return "发布成功";
  }
}
