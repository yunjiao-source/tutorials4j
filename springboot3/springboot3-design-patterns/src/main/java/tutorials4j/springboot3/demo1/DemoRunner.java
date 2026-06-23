package tutorials4j.springboot3.demo1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.demo1.basic.BasicHandlerChain;
import tutorials4j.springboot3.demo1.functional.FunctionalHandlerChain;
import tutorials4j.springboot3.demo1.inject.InjectHandlerChain;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DemoRunner implements CommandLineRunner {
  private final BasicHandlerChain basicHandlerChain;
  private final InjectHandlerChain injectHandlerChain;
  private final FunctionalHandlerChain functionalHandlerChain;

  @Override
  public void run(String... args) throws Exception {
    log.info(">>> 经典责任链");
    Request request = new Request();
    request.setToken("valid-token");
    request.setData("Hello Chain");
    basicHandlerChain.execute(request);

    log.info(">>> 注入式责任链（快速失败）");
    RequestContext context = new RequestContext();
    context.setRequest(request);
    try {
      injectHandlerChain.execute(context);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }

    log.info(">>> 函数式责任链（快速失败）");
    try {
      functionalHandlerChain.execute(request);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
