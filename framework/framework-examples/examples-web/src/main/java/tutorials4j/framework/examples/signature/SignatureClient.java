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
 * 签名示例客户端。
 *
 * <p>封装基于 {@link SignatureUtils} 的签名生成逻辑，向目标接口发起携带签名请求头的 HTTP 调用。
 *
 * @author Yun Jiao
 */
@Service
public class SignatureClient {
  /** 示例应用的应用标识。 */
  private static final String APP_KEY = "appkey1";

  /** 示例应用的签名密钥。 */
  private static final String APP_SECRET = "appSecret1";

  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * 向指定地址发送携带签名请求头的 POST 请求。
   *
   * @param baseUrl 服务基础地址
   * @param path 请求路径
   * @param body 请求体对象
   * @param responseType 响应类型
   * @param <T> 响应类型
   * @return 响应结果
   * @throws JsonProcessingException 请求体序列化失败时抛出
   */
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

  /**
   * 调用签名支付接口进行测试。
   *
   * @param userId 用户 ID
   * @param amount 支付金额
   * @return 接口返回结果
   */
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
