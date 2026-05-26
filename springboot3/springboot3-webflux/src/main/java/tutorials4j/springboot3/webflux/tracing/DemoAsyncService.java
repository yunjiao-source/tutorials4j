package tutorials4j.springboot3.webflux.tracing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 异步服务
 *
 * @author Yun Jiao
 */
@Slf4j
@Service
public class DemoAsyncService {

  @Async
  public void async() {
    log.info("Async...");
  }
}
