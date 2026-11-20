package tutorials4j.service.ai.session.model;

import java.time.Instant;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record MessageDto(String role, String text, Instant createdAt) {}
