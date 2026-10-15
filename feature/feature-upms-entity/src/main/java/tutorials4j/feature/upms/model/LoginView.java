package tutorials4j.feature.upms.model;

import lombok.Builder;

/**
 * 登录页视图模型：用于在登录页展示正在发起授权请求的应用信息。
 *
 * @author YunJiao Yang
 */
@Builder
public record LoginView(String clientId, String clientName) {}
