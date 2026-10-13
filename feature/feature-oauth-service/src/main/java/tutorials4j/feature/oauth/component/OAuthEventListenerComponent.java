package tutorials4j.feature.oauth.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.event.UserSessionEvent;
import tutorials4j.feature.oauth.event.remote.LoginFailRemoteEvent;
import tutorials4j.feature.oauth.event.remote.LoginSuccessRemoteEvent;
import tutorials4j.feature.oauth.event.remote.LogoutRemoteEvent;
import tutorials4j.toolkit.core.util.SpringUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthEventListenerComponent {
  private final BusProperties busProperties;

  @Async
  @EventListener
  public void loginEventHandler(UserSessionEvent userSessionEvent) {
    if (log.isDebugEnabled()) {
      log.debug("发布事件：{}", userSessionEvent);
    }

    switch (userSessionEvent.getType()) {
      case logout -> {
        LogoutRemoteEvent event =
            new LogoutRemoteEvent(this, busProperties.getId(), userSessionEvent);
        SpringUtils.getApplicationContext().publishEvent(event);
      }
      case loginSuccess -> {
        LoginSuccessRemoteEvent event =
            new LoginSuccessRemoteEvent(this, busProperties.getId(), userSessionEvent);
        SpringUtils.getApplicationContext().publishEvent(event);
      }
      case loginFailure -> {
        LoginFailRemoteEvent event =
            new LoginFailRemoteEvent(this, busProperties.getId(), userSessionEvent);
        SpringUtils.getApplicationContext().publishEvent(event);
      }
    }
  }
}
