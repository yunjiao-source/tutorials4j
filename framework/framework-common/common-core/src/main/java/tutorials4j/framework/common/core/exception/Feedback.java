package tutorials4j.framework.common.core.exception;

import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record Feedback(String code, String message) {}
