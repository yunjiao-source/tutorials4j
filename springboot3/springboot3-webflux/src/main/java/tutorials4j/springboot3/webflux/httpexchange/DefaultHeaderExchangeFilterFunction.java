package tutorials4j.springboot3.webflux.httpexchange;

import java.time.LocalDateTime;
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
public class DefaultHeaderExchangeFilterFunction implements ExchangeFilterFunction {

  @Override
  public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction nextFilter) {
    ClientRequest modified =
        ClientRequest.from(clientRequest)
            .header("X-REQUEST-TIMESTAMP", LocalDateTime.now().toString())
            .build();
    return nextFilter.exchange(modified);
  }
}
