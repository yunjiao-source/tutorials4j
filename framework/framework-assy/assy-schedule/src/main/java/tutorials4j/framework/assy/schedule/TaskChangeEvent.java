package tutorials4j.framework.assy.schedule;

import java.time.LocalDateTime;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public record TaskChangeEvent(String name, TaskStatus changeType, LocalDateTime timestape) {}
