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
 * Trace（链路追踪）示例控制器。
 *
 * <p>提供普通接口与 WebFlux（Mono）接口的链路追踪演示，覆盖正常调用、异常抛出及 异步服务调用等场景。
 *
 * @author yangyunjiao
 */
@Slf4j
@RestController
@RequestMapping("trace")
@RequiredArgsConstructor
public class TraceController {
  private final TraceService traceService;

  /**
   * 普通同步接口，内部调用 {@link TraceService#logger()} 演示链路传递。
   *
   * @return 固定返回 "ok"
   * @throws IOException 调用链路中发生 I/O 错误
   */
  @GetMapping("common/get")
  public String commonGet() throws IOException {
    log.info("commonGet");
    traceService.logger();
    return "ok";
  }

  /** 普通接口，抛出内部服务器错误以演示异常链路。 */
  @GetMapping("common/exception")
  public String commonException() {
    log.info("commonException");
    throw BaseErrorCode.INTERNAL_SERVER_ERROR.throwed();
  }

  /** WebFlux 接口，内部调用 {@link TraceService#logger()} 后返回响应式结果。 */
  @GetMapping("/mono/get1")
  public Mono<String> monoGet1() {
    log.info("monoGet1");
    traceService.logger();
    return Mono.just("Hello, WebFlux!");
  }

  /** WebFlux 接口，直接返回响应式结果。 */
  @GetMapping("/mono/get2")
  public Mono<String> monoGet2() {
    log.info("monoGet2");
    return Mono.just("Hello, WebFlux!");
  }

  /** WebFlux 接口，抛出运行时异常以演示异常链路。 */
  @GetMapping("/mono/exception")
  public Mono<String> monoException() {
    log.info("monoException");
    throw new RuntimeException("异常");
  }

  /** 普通接口，不经过异步服务调用，作为对照场景。 */
  @GetMapping("non-logger")
  public String nonLogger() throws IOException {
    log.info("TraceController");
    return "ok";
  }
}
