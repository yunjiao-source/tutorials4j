package tutorials4j.framework.examples;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 示例
 *
 * @author Yun Jiao
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/web-client")
public class WebClientController {
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


}
