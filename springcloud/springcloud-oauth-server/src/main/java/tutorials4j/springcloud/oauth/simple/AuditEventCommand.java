package tutorials4j.springcloud.oauth.simple;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Builder
public class AuditEventCommand {
  private String eventType;
  private String subject;
  private Instant eventTime;
  private String errorCode;
  private String result;
}
