package tutorials4j.framework.examples;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest-template")
public class RestTemplateController {
    private final RestTemplate restTemplate;

    @GetMapping("/get")
    public String testGet() {
        // 调用第三方接口
        String url = "https://jsonplaceholder.typicode.com/posts/1";
        // getForObject(请求地址, 返回值类型)
        String result = restTemplate.getForObject(url, String.class);
        return "GET响应结果：" + result;
    }

    @GetMapping("/get/{id}")
    public String testGetPath(@PathVariable("id") Integer id) {
        String url = "https://jsonplaceholder.typicode.com/posts/{1}";
        // 路径参数按顺序填充
        return restTemplate.getForObject(url, String.class, id);
    }

    @GetMapping("/get/params")
    public String testGetParams() {
        String url = "https://jsonplaceholder.typicode.com/posts?id={id}&userId={userId}";
        // 多参数按顺序传入
        return restTemplate.getForObject(url, String.class, 1, 1);
    }

    @GetMapping("/get/entity")
    public String testGetEntity() {
        String url = "https://jsonplaceholder.typicode.com/posts/1";
        // getForEntity 返回 ResponseEntity（封装完整响应）
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // 获取状态码
        System.out.println("状态码：" + response.getStatusCode());
        // 获取响应头
        System.out.println("响应头：" + response.getHeaders());
        // 获取响应体
        return response.getBody();
    }

    @PostMapping("/post")
    public PostResponse testPost() {
        String url = "https://jsonplaceholder.typicode.com/posts";
        // 构造请求体
        PostRequest request = new PostRequest("测试标题", "测试内容", 1);
        // postForObject(地址, 请求体对象, 返回值类型)
        return restTemplate.postForObject(url, request, PostResponse.class);
    }

    @GetMapping("/exchange")
    public String testExchange() {
        String url = "https://jsonplaceholder.typicode.com/posts";

        // 1. 自定义请求头（token、Content-Type 等）
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", "123456"); // 自定义请求头

        // 2. 构造请求体
        PostRequest request = new PostRequest("exchange测试", "测试内容", 1);
        HttpEntity<PostRequest> httpEntity = new HttpEntity<>(request, headers);

        // 3. 发送请求
        // exchange(地址, 请求方法, 请求实体, 返回值类型, 参数)
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        return response.getBody();
    }


    // 请求实体
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostRequest {
        private String title;
        private String body;
        private Integer userId;
    }

    // 响应实体
    @Data
    public static class PostResponse {
        private Integer id;
        private String title;
        private String body;
        private Integer userId;
    }
}
