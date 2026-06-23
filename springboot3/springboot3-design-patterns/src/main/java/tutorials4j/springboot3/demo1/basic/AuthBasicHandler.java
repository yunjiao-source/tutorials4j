package tutorials4j.springboot3.demo1.basic;

import org.springframework.stereotype.Component;
import tutorials4j.springboot3.demo1.Request;

// 认证处理器
@Component
public class AuthBasicHandler extends AbstractBasicHandler {
  @Override
  public void handle(Request request) {
    if ("valid-token".equals(request.getToken())) {
      System.out.println("[AuthHandler] 认证通过");
      doNext(request);
    } else {
      throw new IllegalArgumentException("认证失败");
    }
  }
}
