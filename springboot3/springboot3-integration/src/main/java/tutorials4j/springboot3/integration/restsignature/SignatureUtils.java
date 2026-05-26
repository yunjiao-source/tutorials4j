package tutorials4j.springboot3.integration.restsignature;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

/**
 * 签名工具服务
 *
 * @author Yun Jiao
 */
public final class SignatureUtils {
  private static final String HMAC_SHA256 = "HmacSHA256";

  public static String generate(
      String appKey, String timestamp, String nonce, String method, String path, String body) {
    String message = buildSignMessage(appKey, timestamp, nonce, method, path, body);
    return hmacSha256(AppKeyCache.getSecret(appKey), message);
  }

  public static boolean verify(
      String appKey,
      String timestamp,
      String nonce,
      String method,
      String path,
      String body,
      String signature) {
    String expectedSignature = generate(appKey, timestamp, nonce, method, path, body);
    return expectedSignature.equals(signature);
  }

  private static String buildSignMessage(
      String appKey, String timestamp, String nonce, String method, String path, String body) {
    // 参数排序并拼接
    Map<String, String> params = new TreeMap<>();
    params.put("appKey", appKey);
    params.put("timestamp", timestamp);
    params.put("nonce", nonce);
    params.put("method", method);
    params.put("path", path);
    if (StringUtils.isNotBlank(body)) {
      params.put("body", body);
    }

    return params.entrySet().stream()
        .map(e -> e.getKey() + "=" + e.getValue())
        .collect(Collectors.joining("&"));
  }

  private static String hmacSha256(String key, String message) {
    try {
      Mac mac = Mac.getInstance(HMAC_SHA256);
      SecretKeySpec secretKeySpec =
          new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
      mac.init(secretKeySpec);
      byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
      return Hex.encodeHexString(hash);
    } catch (Exception e) {
      throw new RuntimeException("签名生成失败", e);
    }
  }
}
