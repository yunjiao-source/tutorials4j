package tutorials4j.framework.examples.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/client/rest-client")
public class RestClientDemoController {
    private final RestClient restClient;

    @GetMapping("/get")
    public String testGet() {
        // 调用第三方接口
        String path = "/posts/1";
        // getForObject(请求地址, 返回值类型)
        String result = restClient.get()
                .uri(path)
                .retrieve()
                .body(String.class);
        return "GET响应结果：" + result;
    }

    @GetMapping("/get/{id}")
    public String testGetPath(@PathVariable("id") Integer id) {
        String path = "/posts/{1}";
        // 路径参数按顺序填充
        String result = restClient.get()
                .uri(path, 1)
                .retrieve()
                .body(String.class);
        return "GET响应结果：" + result;
    }

    @PostMapping("/post")
    public PostResponse testPost() {
        String path = "/posts";
        // 构造请求体
        PostRequest request = new PostRequest("测试标题", "测试内容", 1);
        return restClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PostResponse.class);
    }

}
