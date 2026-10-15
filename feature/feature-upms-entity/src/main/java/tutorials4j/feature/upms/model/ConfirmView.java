package tutorials4j.feature.upms.model;

import java.util.Map;
import lombok.Builder;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Builder
public record ConfirmView(String clientId, String clientName, Map<String, String> scopeNames) {}
