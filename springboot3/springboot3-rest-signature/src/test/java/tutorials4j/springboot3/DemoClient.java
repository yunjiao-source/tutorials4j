package tutorials4j.springboot3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

/**
 * 示例客户端
 *
 * @author Yun Jiao
 */
public class DemoClient {
    private final static String APP_KEY = "your_app_key";
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();

    public  <T> T post(String baseUrl, String path, Object body, Class<T> responseType) throws JsonProcessingException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String nonce = UUID.randomUUID().toString().replace("-", "");
        String bodyJson = objectMapper.writeValueAsString(body);

        // 生成签名
        String signature = SignatureUtils.generate(APP_KEY, timestamp, nonce, "POST", path, bodyJson);

        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-App-Key", APP_KEY);
        headers.set("X-Timestamp", timestamp);
        headers.set("X-Nonce", nonce);
        headers.set("X-Signature", signature);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 发送请求
        HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);
        return restTemplate.postForObject(baseUrl + path, entity, responseType);
    }

    @Test
    public void test() throws JsonProcessingException {
        Map<String, Object> params = Map.of("userId", "tom", "amount", 1000L);

        String rslt = post("http://localhost:8080","/signature/pay", params, String.class);
        System.out.println("返回：" + rslt);
    }
}
