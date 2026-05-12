package tutorials4j.springboot3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 支付状态
 *
 * @author Yun Jiao
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PayStatus {

    WAITING(0, "待支付"),

    SUCCESS(1, "成功"),

    FAILED(2, "失败"),

    CANCELED(3, "已取消"),

    REFUND(4, "已退款");

    private Integer code;

    private String desc;

    public static PayStatus getByCode(String code) {
        for (PayStatus item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}

