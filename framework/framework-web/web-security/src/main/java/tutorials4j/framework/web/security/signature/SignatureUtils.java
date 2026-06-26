package tutorials4j.framework.web.security.signature;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.web.core.exception.WebErrorCode;

/**
 * 签名工具类，提供签名生成和验证功能。
 *
 * <p>签名算法采用 HmacSHA256，参与签名的参数包括 appKey、timestamp、nonce、method、path 和 body（可选）， 参数按字典序排序后拼接成
 * key=value 格式并以 & 连接。
 *
 * @author Yun Jiao
 */
public final class SignatureUtils {
  private static final String HMAC_SHA256 = "HmacSHA256";

  /**
   * 生成签名。
   *
   * @param appKey 应用标识
   * @param appSecret 应用密钥
   * @param timestamp 时间戳（毫秒）
   * @param nonce 随机字符串
   * @param method HTTP 方法（GET、POST 等）
   * @param path 请求路径
   * @param body 请求体内容，可为空
   * @return 十六进制字符串形式的签名
   */
  public static String generate(
      String appKey,
      String appSecret,
      String timestamp,
      String nonce,
      String method,
      String path,
      String body) {
    String message = buildSignMessage(appKey, timestamp, nonce, method, path, body);
    return hmacSha256(appSecret, message);
  }

  /**
   * 验证签名是否有效。
   *
   * @param appKey 应用标识
   * @param appSecret 应用密钥
   * @param timestamp 时间戳
   * @param nonce 随机字符串
   * @param method HTTP 方法
   * @param path 请求路径
   * @param body 请求体内容
   * @param signature 待验证的签名
   * @return 签名匹配返回 true，否则返回 false
   */
  public static boolean verify(
      String appKey,
      String appSecret,
      String timestamp,
      String nonce,
      String method,
      String path,
      String body,
      String signature) {
    String expectedSignature = generate(appKey, appSecret, timestamp, nonce, method, path, body);
    return expectedSignature.equals(signature);
  }

  /**
   * 构建待签名字符串。
   *
   * <p>参数按字典序排序，格式为 key1=value1&key2=value2…，其中 body 仅在非空时加入。
   *
   * @param appKey 应用标识
   * @param timestamp 时间戳
   * @param nonce 随机字符串
   * @param method HTTP 方法
   * @param path 请求路径
   * @param body 请求体（可能为空）
   * @return 排序后拼接的参数字符串
   */
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

  /**
   * 使用 HmacSHA256 算法对消息进行加密。
   *
   * @param secret 密钥
   * @param message 原始消息
   * @return 十六进制加密结果
   */
  private static String hmacSha256(String secret, String message) {
    try {
      Mac mac = Mac.getInstance(HMAC_SHA256);
      SecretKeySpec secretKeySpec =
          new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
      mac.init(secretKeySpec);
      byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
      return Hex.encodeHexString(hash);
    } catch (Exception e) {
      throw WebErrorCode.WEB_SIGNATURE_GENERATE_FAILURE.throwed(e);
    }
  }
}
