package tutorials4j.service.ai.session.model;

import java.util.List;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record HistoryResponse(String sessionId, List<MessageDto> messages) {}
