package tutorials4j.framework.common.core.exception;

import lombok.Builder;

/**
 * 错误反馈对象，封装错误码与错误提示信息。
 *
 * <p>以不可变记录（record）形式表示，可通过 {@code FeedbackBuilder} 构建。
 *
 * @param code 错误码
 * @param message 错误提示信息
 * @author Yun Jiao
 */
@Builder
public record Feedback(String code, String message) {}
