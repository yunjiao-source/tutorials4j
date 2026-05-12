package tutorials4j.springboot3.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tutorials4j.springboot3.request.BaseRequest;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 退款请求
 *
 * @author Yun Jiao
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RefundRequest extends BaseRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 交易订单号
     */
    private String orderNo;

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 交易金额
     */
    private BigDecimal payAmount;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款回调地址
     */
    private String notifyUrl;

}
