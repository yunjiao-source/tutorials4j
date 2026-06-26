package tutorials4j.framework.examples.trace;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import tutorials4j.framework.common.core.exception.BaseErrorCode;

/**
 * trace 示例接口
 *
 * @author yangyunjiao
 */
@Slf4j
@RestController
@RequestMapping("trace")
@RequiredArgsConstructor
public class TraceController {
  private final TraceService traceService;

  @GetMapping("common/get")
  public String commonGet() throws IOException {
    log.info("commonGet");
    traceService.logger();
    return "ok";
  }

  @GetMapping("common/exception")
  public String commonException() {
    log.info("commonException");
    throw BaseErrorCode.NOT_ACCEPTABLE.throwed("异常");
  }

  @GetMapping("/mono/get1")
  public Mono<String> monoGet1() {
    log.info("monoGet1");
    traceService.logger();
    return Mono.just("Hello, WebFlux!");
  }

  @GetMapping("/mono/get2")
  public Mono<String> monoGet2() {
    log.info("monoGet2");
    return Mono.just("Hello, WebFlux!");
  }

  @GetMapping("/mono/exception")
  public Mono<String> monoException() {
    log.info("monoException");
    throw new RuntimeException("异常");
  }

  @GetMapping("non-logger")
  public String nonLogger() throws IOException {
    log.info("TraceController");
    return "ok";
  }
}
