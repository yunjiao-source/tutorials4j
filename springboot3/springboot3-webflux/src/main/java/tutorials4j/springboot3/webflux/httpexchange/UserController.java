package tutorials4j.springboot3.webflux.httpexchange; // src/main/java/com/example/demo/controller/UserController.java

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserApiClient userApiClient;

  public UserController(@Qualifier("userApiWebClient") UserApiClient userApiClient) {
    this.userApiClient = userApiClient;
  }

  @GetMapping
  public Flux<User> getAllUsers(UserQuery query) {
    return userApiClient.getAllUsers(query);
  }

  @GetMapping("/{id}")
  public Mono<User> getUserById(@PathVariable("id") Long id) {
    return userApiClient.getById(id);
  }

  @PostMapping
  public Mono<User> createUser(@RequestBody User user) {
    return userApiClient.save(user);
  }

  @PutMapping("/{id}")
  public Mono<User> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
    return userApiClient.update(id, user);
  }

  @DeleteMapping("/{id}")
  public Mono<Void> deleteUser(@PathVariable("id") Long id) {
    return userApiClient.delete(id);
  }
}
