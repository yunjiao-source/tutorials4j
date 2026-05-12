package tutorials4j.springboot3.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tutorials4j.springboot3.model.PayStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 查询响应
 *
 * @author Yun Jiao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 三方交易订单号
     */
    private String transactionId;

    /**
     * 状态
     */
    private PayStatus payStatus;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    public static QueryResponse failure(String errorCode, String errorMsg) {
        QueryResponse response = new QueryResponse();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setErrorMsg(errorMsg);
        return response;
    }
}
