package tutorials4j.springcloud.oauth.simple;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/** 登录增强：风控、审计、设备信息 */
@Component
@RequiredArgsConstructor
public class AuthenticationAuditListener {

  private final AuditEventService auditEventService;

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
