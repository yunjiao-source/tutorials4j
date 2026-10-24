package tutorials4j.toolkit.http.service.support;

import cn.dev33.satoken.same.SaSameUtil;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import tutorials4j.toolkit.core.constant.DefaultConsts;
import tutorials4j.toolkit.core.util.SecurityUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class HeaderAppendClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    HttpHeaders headers = request.getHeaders();
    headers.set(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
    headers.set(DefaultConsts.HTTP_HEADER_LOGIN_ID, SecurityUtils.getAccount());
    return execution.execute(request, body);
  }
}
