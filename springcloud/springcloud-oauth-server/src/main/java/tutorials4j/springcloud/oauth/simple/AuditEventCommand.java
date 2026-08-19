package tutorials4j.springcloud.oauth.simple;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

/**
 * 审计事件命令，封装一次认证（登录成功/失败）审计事件的数据。
 *
 * <p>由认证事件监听器构建，交由 {@link AuditEventService} 记录。
 *
 * @author Yun Jiao
 */
@Data
@Builder
public class AuditEventCommand {
  /** 事件类型，如 LOGIN_SUCCESS、LOGIN_FAILURE。 */
  private String eventType;

  /** 事件主体（用户名或客户端标识）。 */
  private String subject;

  /** 事件发生时间。 */
  private Instant eventTime;

  /** 错误码（失败时对应异常类型名）。 */
  private String errorCode;

  /** 认证结果，如 SUCCESS、FAIL。 */
  private String result;
}
