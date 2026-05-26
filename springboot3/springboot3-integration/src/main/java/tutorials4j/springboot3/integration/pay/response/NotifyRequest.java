package tutorials4j.springboot3.integration.pay.response;

import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tutorials4j.springboot3.integration.pay.model.PayChannel;
import tutorials4j.springboot3.integration.pay.model.TradeMethod;
import tutorials4j.springboot3.integration.pay.request.BaseRequest;

/**
 * 回调处理请求
 *
 * @author Yun Jiao
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class NotifyRequest extends BaseRequest implements Serializable {

  /** 支付渠道 */
  private PayChannel payChannel;

  /** 交易方式 */
  private TradeMethod method;

  /** 请求体 */
  private HttpServletRequest servletRequest;
}
