package tutorials4j.framework.examples;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/web-client")
public class WebClientDemoController {
    private final WebClient webClient;

    @GetMapping("/get")
    public String testGet() {
        // 调用第三方接口
        String path = "/posts/1";
        // getForObject(请求地址, 返回值类型)
        String result = webClient.get()
                .uri(path)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return "GET响应结果：" + result;
    }

    @PostMapping("/post")
    public Mono<PostResponse> testPost() {
        String path = "/posts";
        // 构造请求体
        PostRequest request = new PostRequest("测试标题", "测试内容", 1);
        return webClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PostResponse.class);
    }


}
