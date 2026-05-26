package tutorials4j.springboot3.integration.pay.response;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一支付响应
 *
 * @author Yun Jiao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  /** 是否成功 */
  private boolean success;

  /** 错误码 */
  private String errorCode;

  /** 错误信息 */
  private String errorMsg;

  /** 支付所需参数（前端调起支付所需） */
  private Map<String, Object> payParams;

  /** 预支付交易会话标识 */
  private String prepayId;

  /** 支付跳转URL */
  private String payUrl;

  /** 二维码链接 */
  private String codeUrl;

  public static PayResponse failure(String errorCode, String errorMsg) {
    PayResponse response = new PayResponse();
    response.setSuccess(false);
    response.setErrorCode(errorCode);
    response.setErrorMsg(errorMsg);
    return response;
  }
}
