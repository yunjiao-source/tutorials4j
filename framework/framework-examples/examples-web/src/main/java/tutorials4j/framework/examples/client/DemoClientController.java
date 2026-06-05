package tutorials4j.framework.examples.client;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class DemoClientController {

  private final RestTemplate restTemplate;
  private final RestClient restClient;
  private final WebClient webClient;

  /** 使用 RestTemplate 获取数据 */
  @GetMapping("/rest-template")
  public List<Post> getPostsWithRestTemplate() {
    String url = "https://jsonplaceholder.typicode.com/posts";
    log.info("RestTemplate 调用外部接口");
    Post[] posts = restTemplate.getForObject(url, Post[].class);
    return List.of(posts);
  }

  /** 使用 RestClient 获取数据 */
  @GetMapping("/rest-client")
  public List<Post> getPostsWithRestClient() {
    log.info("RestClient 调用外部接口");
    List<Post> posts =
        restClient
            .get()
            .uri("/posts") // baseUrl 已在 Config 中配置：https://jsonplaceholder.typicode.com
            .retrieve()
            .body(new ParameterizedTypeReference<List<Post>>() {});
    return posts;
  }

  /** 使用 WebClient 获取数据 */
  @GetMapping("/web-client")
  public Mono<List<Post>> getPostsWithWebClient() {
    log.info("WebClient 调用外部接口");
    return webClient
        .get()
        .uri("/posts")
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<Post>>() {});
  }
}
