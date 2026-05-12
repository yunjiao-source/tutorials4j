package tutorials4j.springboot3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 交易方式
 *
 * @author Yun Jiao
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum TradeMethod {

    PAY("pay", "支付"),
    REFUND("refund", "退款");

    private String code;

    private String desc;

    public static TradeMethod getByCode(String code) {
        for (TradeMethod item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}
