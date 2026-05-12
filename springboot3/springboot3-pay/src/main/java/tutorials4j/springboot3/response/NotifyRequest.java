package tutorials4j.springboot3.response;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tutorials4j.springboot3.model.PayChannel;
import tutorials4j.springboot3.model.TradeMethod;
import tutorials4j.springboot3.request.BaseRequest;

import java.io.Serializable;

/**
 * 回调处理请求
 *
 * @author Yun Jiao
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class NotifyRequest extends BaseRequest implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 支付渠道
     */
    private PayChannel payChannel;

    /**
     * 交易方式
     */
    private TradeMethod method;

    /**
     * 请求体
     */
    private HttpServletRequest servletRequest;

}
