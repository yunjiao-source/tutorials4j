package tutorials4j.framework.examples.signature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.web.security.signature.SignatureUtils;

/**
 * 示例客户端
 *
 * @author Yun Jiao
 */
@Service
public class SignatureClient {
  private static final String APP_KEY = "appkey1";
  private static final String APP_SECRET = "appSecret1";
  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper();

  private <T> T post(String baseUrl, String path, Object body, Class<T> responseType)
      throws JsonProcessingException {
    String timestamp = String.valueOf(System.currentTimeMillis());
    String nonce = UUID.randomUUID().toString().replace("-", "");
    String bodyJson = objectMapper.writeValueAsString(body);

    // 生成签名
    String signature =
        SignatureUtils.generate(APP_KEY, APP_SECRET, timestamp, nonce, "POST", path, bodyJson);

    // 构建请求头
    HttpHeaders headers = new HttpHeaders();
    headers.set(DefaultConsts.HTTP_HEADER_SIGNATURE_APP_KEY, APP_KEY);
    headers.set(DefaultConsts.HTTP_HEADER_SIGNATURE_TIMESTAMP, timestamp);
    headers.set(DefaultConsts.HTTP_HEADER_SIGNATURE_NONCE, nonce);
    headers.set(DefaultConsts.HTTP_HEADER_SIGNATURE, signature);
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 发送请求
    HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);
    return restTemplate.postForObject(baseUrl + path, entity, responseType);
  }

  public String test(String userId, Long amount) {
    Map<String, Object> params = Map.of("userId", userId, "amount", amount);

    String rst = null;
    try {
      rst = post("http://localhost:8080", "/signature/pay", params, String.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return rst;
  }
}
