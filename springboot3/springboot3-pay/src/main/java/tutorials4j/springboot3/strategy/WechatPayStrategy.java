package tutorials4j.springboot3.strategy;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.app.AppServiceExtension;
import com.wechat.pay.java.service.payments.app.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.app.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.*;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.springboot3.model.*;
import tutorials4j.springboot3.request.PayRequest;
import tutorials4j.springboot3.request.RefundRequest;
import tutorials4j.springboot3.response.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 微信支付实现
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class WechatPayStrategy extends AbstractPayStrategy {

    @Override
    protected PayResponse doPay(PayRequest request) {
        try {
            // 初始化商户配置
            Config config = new RSAAutoCertificateConfig.Builder()
                    .merchantId(request.getMchId())
                    .privateKey(request.getPrivateKey())
                    .merchantSerialNumber(request.getSerialNo())
                    .apiV3Key(request.getApiV3Key())
                    .build();

            switch (request.getTradeType()) {
                case APP:
                    com.wechat.pay.java.service.payments.app.model.PrepayRequest appRrq = new com.wechat.pay.java.service.payments.app.model.PrepayRequest();
                    appRrq.setAppid(request.getAppId());
                    appRrq.setMchid(request.getMchId());
                    appRrq.setOutTradeNo(request.getOrderNo());
                    appRrq.setDescription(request.getDescription());
                    appRrq.setAttach(request.getAttach());
                    appRrq.setNotifyUrl(request.getNotifyUrl());
                    com.wechat.pay.java.service.payments.app.model.Amount appAmount = new com.wechat.pay.java.service.payments.app.model.Amount();
                    appAmount.setCurrency("CNY");
                    appAmount.setTotal(request.getAmount().multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue());
                    appRrq.setAmount(appAmount);
                    if (Objects.nonNull(request.getExpireTime())) {
                        appRrq.setTimeExpire(request.getExpireTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    }

                    log.info("APP支付，请求参数:{}", appRrq);
                    com.wechat.pay.java.service.payments.app.model.PrepayWithRequestPaymentResponse appRes = new AppServiceExtension.Builder().config(config).build().prepayWithRequestPayment(appRrq);
                    log.info("APP支付，返回参数:{}", appRes);

                    if (Objects.nonNull(appRes)) {
                        Map<String, Object> appParams = new HashMap<>();
                        appParams.put("appid", appRes.getAppid());
                        appParams.put("partnerid", appRes.getPartnerId());
                        appParams.put("prepayid", appRes.getPrepayId());
                        appParams.put("package", appRes.getPackageVal());
                        appParams.put("noncestr", appRes.getNonceStr());
                        appParams.put("timestamp", appRes.getTimestamp());
                        appParams.put("sign", appRes.getSign());
                        return PayResponse.builder().success(true).payParams(appParams).prepayId(appRes.getPrepayId()).build();
                    }
                    return PayResponse.failure(Constants.OPERATE_ERROR_MSG, Constants.OPERATE_ERROR_MSG);
                case JSAPI:
                    com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest jsapiReq = new com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest();
                    jsapiReq.setAppid(request.getAppId());
                    jsapiReq.setMchid(request.getMchId());
                    jsapiReq.setOutTradeNo(request.getOrderNo());
                    jsapiReq.setDescription(request.getDescription());
                    jsapiReq.setAttach(request.getAttach());
                    jsapiReq.setNotifyUrl(request.getNotifyUrl());
                    com.wechat.pay.java.service.payments.jsapi.model.Amount jsapiAmount = new com.wechat.pay.java.service.payments.jsapi.model.Amount();
                    jsapiAmount.setCurrency("CNY");
                    jsapiAmount.setTotal(request.getAmount().multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue());
                    jsapiReq.setAmount(jsapiAmount);
                    com.wechat.pay.java.service.payments.jsapi.model.Payer jsapiPayer = new com.wechat.pay.java.service.payments.jsapi.model.Payer();
                    jsapiPayer.setOpenid(request.getOpenId());
                    jsapiReq.setPayer(jsapiPayer);
                    if (Objects.nonNull(request.getExpireTime())) {
                        jsapiReq.setTimeExpire(request.getExpireTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    }

                    log.info("JSAPI支付，请求参数:{}", jsapiReq);
                    com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse jsapiRes = new JsapiServiceExtension.Builder().config(config).build().prepayWithRequestPayment(jsapiReq);
                    log.info("JSAPI支付，返回参数:{}", jsapiRes);

                    if (Objects.nonNull(jsapiRes)) {
                        Map<String, Object> jsapiParams = new HashMap<>();
                        jsapiParams.put("appId", jsapiRes.getAppId());
                        jsapiParams.put("paySign", jsapiRes.getPaySign());
                        jsapiParams.put("signType", jsapiRes.getSignType());
                        jsapiParams.put("package", jsapiRes.getPackageVal());
                        jsapiParams.put("nonceStr", jsapiRes.getNonceStr());
                        jsapiParams.put("timeStamp", jsapiRes.getTimeStamp());
                        return PayResponse.builder().success(true).payParams(jsapiParams).build();
                    }
                    return PayResponse.failure(Constants.OPERATE_ERROR_MSG, Constants.OPERATE_ERROR_MSG);
                case NATIVE:
                    com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest nativeReq = new com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest();
                    nativeReq.setAppid(request.getAppId());
                    nativeReq.setMchid(request.getMchId());
                    nativeReq.setOutTradeNo(request.getOrderNo());
                    nativeReq.setDescription(request.getDescription());
                    nativeReq.setAttach(request.getAttach());
                    nativeReq.setNotifyUrl(request.getNotifyUrl());
                    com.wechat.pay.java.service.payments.nativepay.model.Amount nativeAmount = new com.wechat.pay.java.service.payments.nativepay.model.Amount();
                    nativeAmount.setCurrency("CNY");
                    nativeAmount.setTotal(request.getAmount().multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue());
                    nativeReq.setAmount(nativeAmount);
                    if (Objects.nonNull(request.getExpireTime())) {
                        nativeReq.setTimeExpire(request.getExpireTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    }

                    log.info("NATIVE支付，请求参数:{}", nativeReq);
                    com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse nativeRes = new NativePayService.Builder().config(config).build().prepay(nativeReq);
                    log.info("NATIVE支付，返回参数:{}", nativeRes);

                    if (Objects.nonNull(nativeRes)) {
                        return PayResponse.builder().success(true).codeUrl(nativeRes.getCodeUrl()).build();
                    }
                case H5:
                    com.wechat.pay.java.service.payments.h5.model.PrepayRequest h5Req = new com.wechat.pay.java.service.payments.h5.model.PrepayRequest();
                    h5Req.setAppid(request.getAppId());
                    h5Req.setMchid(request.getMchId());
                    h5Req.setOutTradeNo(request.getOrderNo());
                    h5Req.setDescription(request.getDescription());
                    h5Req.setAttach(request.getAttach());
                    h5Req.setNotifyUrl(request.getNotifyUrl());

                    com.wechat.pay.java.service.payments.h5.model.Amount h5Amount = new com.wechat.pay.java.service.payments.h5.model.Amount();
                    h5Amount.setCurrency("CNY");
                    h5Amount.setTotal(request.getAmount().multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue());
                    h5Req.setAmount(h5Amount);

                    com.wechat.pay.java.service.payments.h5.model.SceneInfo h5Scene = new com.wechat.pay.java.service.payments.h5.model.SceneInfo();
                    h5Scene.setPayerClientIp(request.getClientIp());
                    com.wechat.pay.java.service.payments.h5.model.H5Info h5Info = new com.wechat.pay.java.service.payments.h5.model.H5Info();
                    h5Info.setType(request.getH5Type());
                    h5Scene.setH5Info(h5Info);
                    h5Req.setSceneInfo(h5Scene);
                    if (Objects.nonNull(request.getExpireTime())) {
                        h5Req.setTimeExpire(request.getExpireTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    }

                    log.info("H5支付，请求参数:{}", h5Req);
                    com.wechat.pay.java.service.payments.h5.model.PrepayResponse h5Res = new H5Service.Builder().config(config).build().prepay(h5Req);
                    log.info("H5支付，返回参数:{}", h5Res);

                    if (Objects.nonNull(h5Res)) {
                        return PayResponse.builder().success(true).codeUrl(h5Res.getH5Url()).build();
                    }
                default:
                    return PayResponse.failure(Constants.OPERATE_ERROR_MSG, "不支持的微信交易类型");
            }
        } catch (Exception e) {
            log.error("微信支付失败: {}", e);
            return PayResponse.failure(Constants.OPERATE_ERROR_MSG, e.getMessage());
        }
    }

    @Override
    protected QueryResponse doQueryOrder(PayRequest request) {
        try {
            QueryOrderByOutTradeNoRequest req = new QueryOrderByOutTradeNoRequest();
            req.setMchid(request.getMchId());
            req.setOutTradeNo(request.getOrderNo());

            log.info("订单开始，请求参数:{}", req);
            Transaction res = new AppServiceExtension.Builder().config(
                    new RSAAutoCertificateConfig.Builder()
                            .merchantId(request.getMchId())
                            .privateKey(request.getPrivateKey())
                            .merchantSerialNumber(request.getSerialNo())
                            .apiV3Key(request.getApiV3Key())
                            .build()
            ).build().queryOrderByOutTradeNo(req);
            log.info("订单结束，返回参数:{}", res);

            if (Objects.nonNull(res)) {
                PayStatus payStatus = PayStatus.WAITING;
                LocalDateTime payTime = null;
                if (Transaction.TradeStateEnum.SUCCESS == res.getTradeState()) {
                    // 成功
                    payTime = LocalDateTime.parse(res.getSuccessTime());
                    payStatus = PayStatus.SUCCESS;
                } else if (Transaction.TradeStateEnum.NOTPAY == res.getTradeState() || Transaction.TradeStateEnum.USERPAYING == res.getTradeState()) {
                    // 未支付、支付中
                    payStatus = PayStatus.WAITING;
                } else if (Transaction.TradeStateEnum.CLOSED == res.getTradeState() || Transaction.TradeStateEnum.REVOKED == res.getTradeState()) {
                    // 已关闭、已撤销
                    payStatus = PayStatus.CANCELED;
                } else if (Transaction.TradeStateEnum.PAYERROR == res.getTradeState()) {
                    // 失败
                    payStatus = PayStatus.FAILED;
                }

                return QueryResponse.builder()
                        .success(true)
                        .orderNo(res.getOutTradeNo())
                        .transactionId(res.getTransactionId())
                        .payStatus(payStatus)
                        .payTime(payTime)
                        .build();
            }
            return QueryResponse.failure(Constants.OPERATE_ERROR_MSG, "订单查询失败");
        } catch (Exception e) {
            log.error("微信支付查询失败: {}", e);
            return QueryResponse.failure(Constants.OPERATE_ERROR_MSG, e.getMessage());
        }
    }

    @Override
    protected CloseResponse doClose(PayRequest request) {
        try {
            CloseOrderRequest req = new CloseOrderRequest();
            req.setMchid(request.getMchId());
            req.setOutTradeNo(request.getOrderNo());

            log.info("关闭订单开始，请求参数:{}", req);
            new AppServiceExtension.Builder().config(
                    new RSAAutoCertificateConfig.Builder()
                            .merchantId(request.getMchId())
                            .privateKey(request.getPrivateKey())
                            .merchantSerialNumber(request.getSerialNo())
                            .apiV3Key(request.getApiV3Key())
                            .build()
            ).build().closeOrder(req);
            log.info("关闭订单结束");

            return CloseResponse.success(request.getOrderNo());
        } catch (Exception e) {
            log.error("微信关闭订单失败: {}", e);
            return CloseResponse.failure(Constants.OPERATE_ERROR_MSG, e.getMessage());
        }
    }

    @Override
    protected RefundResponse doRefund(RefundRequest request) {
        try {
            CreateRequest req = new CreateRequest();
            req.setOutTradeNo(request.getOrderNo());
            AmountReq amountReq = new AmountReq();
            amountReq.setTotal(request.getPayAmount().multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).longValue());
            amountReq.setRefund(request.getRefundAmount().multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).longValue());
            amountReq.setCurrency("CNY");
            req.setAmount(amountReq);
            req.setOutRefundNo(request.getRefundNo());
            req.setReason(request.getRefundReason());
            req.setNotifyUrl(request.getNotifyUrl());

            log.info("退款开始，请求参数:{}", req);
            Refund res = new RefundService.Builder().config(
                    new RSAAutoCertificateConfig.Builder()
                            .merchantId(request.getMchId())
                            .privateKey(request.getPrivateKey())
                            .merchantSerialNumber(request.getSerialNo())
                            .apiV3Key(request.getApiV3Key())
                            .build()
            ).build().create(req);
            log.info("退款结束，返回参数:{}", res);

            if (Objects.nonNull(res)) {
                return RefundResponse.builder().success(true).refundNo(res.getOutRefundNo()).build();
            }
            return RefundResponse.failure(Constants.OPERATE_ERROR_MSG, "退款失败");
        } catch (Exception e) {
            log.error("微信退款失败: {}", e);
            return RefundResponse.failure(Constants.OPERATE_ERROR_MSG, e.getMessage());
        }
    }

    @Override
    protected RefundResponse doQueryRefund(RefundRequest request) {
        try {
            QueryByOutRefundNoRequest req = new QueryByOutRefundNoRequest();
            req.setOutRefundNo(request.getRefundNo());

            log.info("退款查询开始，请求参数:{}", req);
            Refund res = new RefundService.Builder().config(
                    new RSAAutoCertificateConfig.Builder()
                            .merchantId(request.getMchId())
                            .privateKey(request.getPrivateKey())
                            .merchantSerialNumber(request.getSerialNo())
                            .apiV3Key(request.getApiV3Key())
                            .build()
            ).build().queryByOutRefundNo(req);
            log.info("退款查询结束，返回参数:{}", res);

            if (Objects.nonNull(res)) {
                PayStatus payStatus = PayStatus.WAITING;
                BigDecimal amount = null;
                LocalDateTime refundTime = null;
                if (Status.SUCCESS == res.getStatus()) {
                    // 成功
                    refundTime = LocalDateTime.parse(res.getSuccessTime());
                    payStatus = PayStatus.SUCCESS;
                } else if (Status.PROCESSING == res.getStatus()) {
                    // 处理中
                    payStatus = PayStatus.WAITING;
                } else if (Status.CLOSED == res.getStatus()) {
                    // 已关闭
                    payStatus = PayStatus.CANCELED;
                } else if (Status.ABNORMAL == res.getStatus()) {
                    // 失败
                    payStatus = PayStatus.FAILED;
                }

                return RefundResponse.builder()
                        .success(true)
                        .orderNo(res.getOutTradeNo())
                        .refundNo(res.getOutRefundNo())
                        .refundId(res.getRefundId())
                        .refundStatus(payStatus)
                        .refundTime(refundTime)
                        .build();
            }
            return RefundResponse.failure(Constants.OPERATE_ERROR_MSG, "退款查询失败");
        } catch (Exception e) {
            log.error("微信退款查询失败: {}", e);
            return RefundResponse.failure(Constants.OPERATE_ERROR_MSG, e.getMessage());
        }
    }

    @Override
    public NotifyResponse handleNotify(NotifyRequest request) {
        try {
            log.info("微信回调处理开始...");
            HttpServletRequest servletRequest = request.getServletRequest();
            // 读取请求体的信息
            ServletInputStream inputStream = servletRequest.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                stringBuffer.append(s);
            }
            log.info("微信回调处理，请求体参数:{}", stringBuffer);

            // 初始化 NotificationParser
            NotificationParser parser = new NotificationParser(
                    new RSAAutoCertificateConfig.Builder()
                            .merchantId(request.getMchId())
                            .privateKey(request.getPrivateKey())
                            .merchantSerialNumber(request.getSerialNo())
                            .apiV3Key(request.getApiV3Key())
                            .build()
            );
            RequestParam requestParam = new RequestParam.Builder()
                    .serialNumber(servletRequest.getHeader("Wechatpay-Serial"))
                    .nonce(servletRequest.getHeader("Wechatpay-Nonce"))
                    .signature(servletRequest.getHeader("Wechatpay-Signature"))
                    .timestamp(servletRequest.getHeader("Wechatpay-Timestamp"))
                    .signType(servletRequest.getHeader("Wechatpay-Signature-Type"))
                    .body(stringBuffer.toString())
                    .build();

            log.info("微信回调处理，RequestParam:{}", requestParam);

            switch (request.getMethod()) {
                case PAY:
                    Transaction transaction = parser.parse(requestParam, Transaction.class);
                    log.info("微信支付回调返回参数:{}", transaction);
                    NotifyResponse payResponse = NotifyResponse.builder().success(true).orderNo(transaction.getOutTradeNo()).build();
                    if (Transaction.TradeStateEnum.SUCCESS == transaction.getTradeState()) {
                        // SUCCESS：支付成功
                        payResponse.setTransactionId(transaction.getTransactionId());
                        payResponse.setTime(LocalDateTime.parse(transaction.getSuccessTime()));
                        payResponse.setStatus(PayStatus.SUCCESS);
                    } else if (Transaction.TradeStateEnum.CLOSED == transaction.getTradeState() || Transaction.TradeStateEnum.REVOKED == transaction.getTradeState() || Transaction.TradeStateEnum.PAYERROR == transaction.getTradeState()) {
                        // CLOSED：已关闭、REVOKED：已撤销、PAYERROR：支付失败
                        payResponse.setStatus(PayStatus.FAILED);
                    } else {
                        // REFUND：转入退款、NOTPAY：未支付、USERPAYING：用户支付中
                        log.info("支付中的暂不处理：{}", transaction);
                    }

                    log.info("微信支付回调结束:{}", payResponse);
                    return payResponse;
                case REFUND:
                    RefundNotification refund = parser.parse(requestParam, RefundNotification.class);
                    log.info("微信退款回调返回参数:{}", refund);
                    NotifyResponse refundResponse = NotifyResponse.builder().success(true).refundNo(refund.getOutRefundNo()).build();
                    if (Status.SUCCESS == refund.getRefundStatus()) {
                        // SUCCESS：退款成功
                        refundResponse.setRefundId(refund.getRefundId());
                        refundResponse.setOrderNo(refund.getOutTradeNo());
                        refundResponse.setTransactionId(refund.getTransactionId());
                        refundResponse.setTime(LocalDateTime.parse(refund.getSuccessTime()));
                        refundResponse.setStatus(PayStatus.SUCCESS);
                    } else {
                        // ABNORMAL：退款异常、CLOSED：退款关闭
                        refundResponse.setRefundId(refund.getRefundId());
                        refundResponse.setOrderNo(refund.getOutTradeNo());
                        refundResponse.setTransactionId(refund.getTransactionId());
                        refundResponse.setTime(LocalDateTime.parse(refund.getCreateTime()));
                        refundResponse.setStatus(PayStatus.FAILED);
                    }

                    log.info("微信退款回调结束:{}", refundResponse);
                    return refundResponse;
                default:
                    return NotifyResponse.failure(Constants.OPERATE_ERROR_MSG, "不支持的回调方式");
            }
        } catch (Exception e) {
            log.error("微信回调处理失败: {}", e);
            return NotifyResponse.failure(Constants.OPERATE_ERROR_MSG, e.getMessage());
        }
    }

    @Override
    public PayChannel getPayChannel() {
        return PayChannel.WECHAT_PAY;
    }

}