package tutorials4j.springcloud.oauth.simple;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * 登录增强监听器：监听认证成功与失败事件并记录审计信息。
 *
 * <p>认证成功时记录 LOGIN_SUCCESS 事件，认证失败时记录 LOGIN_FAILURE 事件（含异常类型）。
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class AuthenticationAuditListener {

  private final AuditEventService auditEventService;

  /**
   * 监听认证成功事件并记录审计信息。
   *
   * @param event 认证成功事件
   */
  @EventListener
  public void onSuccess(AuthenticationSuccessEvent event) {
    auditEventService.record(
        AuditEventCommand.builder()
            .eventType("LOGIN_SUCCESS")
            .subject(event.getAuthentication().getName())
            .eventTime(Instant.now())
            .result("SUCCESS")
            .build());
  }

  /**
   * 监听认证失败事件并记录审计信息（含错误码）。
   *
   * @param event 认证失败事件
   */
  @EventListener
  public void onFailure(AbstractAuthenticationFailureEvent event) {
    auditEventService.record(
        AuditEventCommand.builder()
            .eventType("LOGIN_FAILURE")
            .subject(String.valueOf(event.getAuthentication().getPrincipal()))
            .eventTime(Instant.now())
            .result("FAIL")
            .errorCode(event.getException().getClass().getSimpleName())
            .build());
  }
}
