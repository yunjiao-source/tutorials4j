package tutorials4j.springboot3.strategy;

import tutorials4j.springboot3.model.*;
import tutorials4j.springboot3.request.PayRequest;
import tutorials4j.springboot3.request.RefundRequest;
import tutorials4j.springboot3.response.*;

/**
 * 支付策略接口
 *
 * @author Yun Jiao
 */
public interface PayStrategy {

    /**
     * 获取支付渠道
     */
    PayChannel getPayChannel();

    /**
     * 统一支付
     */
    PayResponse pay(PayRequest request);

    /**
     * 查询订单
     */
    QueryResponse queryOrder(PayRequest request);

    /**
     * 关闭订单
     */
    CloseResponse close(PayRequest request);

    /**
     * 退款
     */
    RefundResponse refund(RefundRequest request);

    /**
     * 退款查询
     */
    RefundResponse queryRefund(RefundRequest request);

    /**
     * 处理回调
     */
    NotifyResponse handleNotify(NotifyRequest request);

}
