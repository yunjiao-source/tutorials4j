package tutorials4j.service.ai.demo.order;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record OrderChat(
    @JsonPropertyDescription("订单号") String orderId,
    @JsonPropertyDescription("用户Id") String userId,
    @JsonPropertyDescription("对话状态") ChatStatus status) {}
