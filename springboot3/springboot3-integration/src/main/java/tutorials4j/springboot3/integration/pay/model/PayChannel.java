package tutorials4j.springboot3.integration.pay.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 支付渠道
 *
 * @author Yun Jiao
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PayChannel {
  WECHAT_PAY("wechat", "微信支付"),
  ALI_PAY("alipay", "支付宝"),
  UNION_PAY("union", "银联支付");

  private String code;

  private String name;

  public static PayChannel getByCode(String code) {
    for (PayChannel item : values()) {
      if (item.getCode().equals(code)) {
        return item;
      }
    }
    return null;
  }
}
