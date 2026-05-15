package tutorials4j.springboot3.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关闭订单响应参数
 *
 * @author Yun Jiao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CloseResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  /** 是否成功 */
  private Boolean success;

  /** 订单号 */
  private String orderNo;

  /** 错误码 */
  private String errorCode;

  /** 错误信息 */
  private String errorMsg;

  public static CloseResponse success(String orderNo) {
    CloseResponse response = new CloseResponse();
    response.setSuccess(true);
    response.setOrderNo(orderNo);
    return response;
  }

  public static CloseResponse failure(String errorCode, String errorMsg) {
    CloseResponse response = new CloseResponse();
    response.setSuccess(false);
    response.setErrorCode(errorCode);
    response.setErrorMsg(errorMsg);
    return response;
  }
}
