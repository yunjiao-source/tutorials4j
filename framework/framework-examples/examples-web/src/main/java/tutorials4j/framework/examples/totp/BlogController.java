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
 * 播客
 *
 * @author Yun Jiao
 */
@RestController
public class BlogController {

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
