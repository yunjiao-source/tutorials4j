package tutorials4j.feature.oauth.model;

import java.util.Map;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record ConfirmView(String clientId, String clientName, Map<String, String> scopeNames) {}
