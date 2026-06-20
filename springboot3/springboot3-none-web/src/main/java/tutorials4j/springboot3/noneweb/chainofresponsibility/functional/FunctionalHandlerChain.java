package tutorials4j.springboot3.noneweb.chainofresponsibility.functional;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.noneweb.chainofresponsibility.Request;
import tutorials4j.springboot3.noneweb.chainofresponsibility.RequestContext;

@Component
public class FunctionalHandlerChain {
  private final List<UnaryOperator<RequestContext>> handlers = new ArrayList<>();

  // 初始化组装链路
  @PostConstruct
  public void init() {
    handlers.add(this::auth);
    handlers.add(this::rateLimit);
    handlers.add(this::business);
  }

  // 执行链路
  public RequestContext execute(Request request) throws Throwable {
    RequestContext ctx = new RequestContext(request);
    for (UnaryOperator<RequestContext> handler : handlers) {
      ctx = handler.apply(ctx);
      if (ctx.getThrowable() != null) {
        throw ctx.getThrowable();
      }
    }
    return ctx;
  }

  // 认证逻辑
  private RequestContext auth(RequestContext ctx) {
    if ("valid-token".equals(ctx.getRequest().getToken())) {
      System.out.println("[Auth] 通过");
    } else {
      ctx.setThrowable(new IllegalArgumentException("认证失败"));
    }
    return ctx;
  }

  // 限流逻辑
  private RequestContext rateLimit(RequestContext ctx) {
    System.out.println("[RateLimit] 通过");
    return ctx;
  }

  // 业务逻辑
  private RequestContext business(RequestContext ctx) {
    System.out.println("[Business] 处理: " + ctx.getRequest().getData());
    return ctx;
  }
}
