package tutorials4j.springboot3.webflux.httpexchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class LoggingExchangeFilterFunction implements ExchangeFilterFunction {

  @Override
  public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction nextFilter) {
    long start = System.nanoTime();
    log.info("🌐 Request: {} {}", clientRequest.method(), clientRequest.url());
    return nextFilter
        .exchange(clientRequest)
        .doOnNext(
            response -> {
              long duration = (System.nanoTime() - start) / 1_000_000;
              log.info("✅ Response: {} ({} ms)", response.statusCode(), duration);
            })
        .doOnError(e -> log.error("❌ Request failed: {}", e.getMessage()));
  }
}
