package tutorials4j.springboot3.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tutorials4j.springboot3.model.PayStatus;

/**
 * 退款响应
 *
 * @author Yun Jiao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  /** 是否成功 */
  private Boolean success;

  /** 交易订单号 */
  private String orderNo;

  /** 退款单号 */
  private String refundNo;

  /** 退款三方订单号 */
  private String refundId;

  /** 状态 */
  private PayStatus refundStatus;

  /** 退款时间 */
  private LocalDateTime refundTime;

  /** 错误码 */
  private String errorCode;

  /** 错误信息 */
  private String errorMsg;

  public static RefundResponse failure(String errorCode, String errorMsg) {
    return RefundResponse.builder().success(false).errorCode(errorCode).errorMsg(errorMsg).build();
  }
}
