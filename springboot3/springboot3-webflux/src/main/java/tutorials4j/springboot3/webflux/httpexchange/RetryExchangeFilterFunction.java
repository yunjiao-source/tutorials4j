package tutorials4j.springboot3.webflux.httpexchange;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class RetryExchangeFilterFunction implements ExchangeFilterFunction {

  @Override
  public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction nextFilter) {
    RetryBackoffSpec spec =
        Retry.backoff(3, Duration.ofSeconds(1))
            .maxBackoff(Duration.ofSeconds(5))
            .doAfterRetry(rs -> log.info("重试 {}", rs.totalRetries()));
    return nextFilter.exchange(clientRequest).retryWhen(spec);
  }
}
