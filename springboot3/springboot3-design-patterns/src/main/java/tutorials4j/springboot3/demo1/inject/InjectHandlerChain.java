package tutorials4j.springboot3.demo1.inject;

import java.util.List;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.demo1.RequestContext;

@Component
public class InjectHandlerChain {
  // Spring自动注入所有实现类，按@Order排序
  private final List<InjectHandler> handlers;

  public InjectHandlerChain(List<InjectHandler> handlers) {
    this.handlers = handlers;
  }

  // 串行执行所有处理器
  public void execute(RequestContext context) throws Throwable {
    for (InjectHandler handler : handlers) {
      if (context.getThrowable() != null) {
        throw context.getThrowable();
      }
      handler.handle(context);
    }
  }
}
