package tutorials4j.springboot3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 交易类型
 *
 * @author Yun Jiao
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum TradeType {
  APP("app", "APP支付"),
  JSAPI("jsapi", "公众号/小程序支付"),
  NATIVE("native", "扫码支付"),
  H5("h5", "H5支付");

  private String code;

  private String desc;

  public static TradeType getByCode(String code) {
    for (TradeType item : values()) {
      if (item.getCode().equals(code)) {
        return item;
      }
    }
    return null;
  }
}
