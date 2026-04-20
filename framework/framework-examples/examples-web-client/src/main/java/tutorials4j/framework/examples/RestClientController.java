package tutorials4j.framework.examples;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest-client")
public class RestClientController {
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


}
