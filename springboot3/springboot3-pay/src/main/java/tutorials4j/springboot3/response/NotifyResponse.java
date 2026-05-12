package tutorials4j.springboot3.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tutorials4j.springboot3.model.PayStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 回调处理响应
 *
 * @author Yun Jiao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotifyResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 交易订单号
     */
    private String orderNo;

    /**
     * 三方交易订单号
     */
    private String transactionId;

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 退款三方订单号
     */
    private String refundId;

    /**
     * 状态
     */
    private PayStatus status;

    /**
     * 时间
     */
    private LocalDateTime time;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    public static NotifyResponse failure(String errorCode, String errorMsg) {
        return NotifyResponse.builder().success(false).errorCode(errorCode).errorMsg(errorMsg).build();
    }

}
