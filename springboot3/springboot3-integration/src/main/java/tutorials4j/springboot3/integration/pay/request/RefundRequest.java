package tutorials4j.springboot3.integration.pay.request;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 退款请求
 *
 * @author Yun Jiao
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RefundRequest extends BaseRequest implements Serializable {

  /** 交易订单号 */
  private String orderNo;

  /** 退款单号 */
  private String refundNo;

  /** 交易金额 */
  private BigDecimal payAmount;

  /** 退款金额 */
  private BigDecimal refundAmount;

  /** 退款原因 */
  private String refundReason;

  /** 退款回调地址 */
  private String notifyUrl;
}
