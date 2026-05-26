package tutorials4j.springboot3.integration.pay.request;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 公共请求参数
 *
 * @author Yun Jiao
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseRequest implements Serializable {

  /** appid */
  private String appId;

  /** 商户号 */
  private String mchId;

  /** 商户证书序列号 */
  private String serialNo;

  /** 商户API私钥 */
  private String privateKey;

  /** 商户API公钥 */
  private String publicKey;

  /** 商户APIV3密钥 */
  private String apiV3Key;
}
