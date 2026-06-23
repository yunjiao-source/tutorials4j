package tutorials4j.springboot3.demo1.inject;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.demo1.RequestContext;

@Order(1)
@Component
public class AuthInjectHandler implements InjectHandler {
  @Override
  public void handle(RequestContext context) {
    if ("valid-token".equals(context.getRequest().getToken())) {
      System.out.println("[AuthHandler] 认证通过");
    } else {
      context.setThrowable(new IllegalArgumentException("认证失败"));
    }
  }
}
