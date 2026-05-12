package tutorials4j.springboot3.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tutorials4j.springboot3.model.PayChannel;
import tutorials4j.springboot3.model.TradeType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 统一支付请求
 *
 * @author Yun Jiao
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PayRequest extends BaseRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付渠道
     */
    private PayChannel payChannel;

    /**
     * 交易类型
     */
    private TradeType tradeType;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单金额（单位：分）
     */
    private BigDecimal amount;

    /**
     * 订单描述
     */
    private String description;

    /**
     * 用户IP
     */
    private String clientIp;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 附加数据
     */
    private String attach;

    /**
     * 通知地址
     */
    private String notifyUrl;

    /**
     * 用户标识
     */
    private String openId;

    /**
     * 场景类型：Wap、iOS、Android
     */
    private String h5Type;

}
