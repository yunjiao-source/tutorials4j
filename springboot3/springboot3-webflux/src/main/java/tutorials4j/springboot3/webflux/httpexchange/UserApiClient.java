package tutorials4j.springboot3.webflux.httpexchange; // src/main/java/com/example/demo/client/UserApiClient.java

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@HttpExchange(url = "/users", accept = "application/json")
public interface UserApiClient {

  @GetExchange
  Flux<User> getAllUsers(UserQuery query);

  @GetExchange("/{id}")
  Mono<User> getById(@PathVariable("id") Long id);

  @PostExchange("/")
  Mono<User> save(@RequestBody User user);

  @PutExchange("/{id}")
  Mono<User> update(@PathVariable("id") Long id, @RequestBody User user);

  @DeleteExchange("/{id}")
  Mono<Void> delete(@PathVariable("id") Long id);
}
