package tutorials4j.microservice.test.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.event.remote.LoginFailRemoteEvent;
import tutorials4j.feature.oauth.event.remote.LoginSuccessRemoteEvent;
import tutorials4j.feature.oauth.event.remote.LogoutRemoteEvent;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@Component
public class Test1Component {
  @EventListener
  public void handleLoginFailRemoteEvent(LoginFailRemoteEvent event) {
    log.info("接收'登录失败'事件：{}", event);
  }

  @EventListener
  public void handleLoginSuccessRemoteEvent(LoginSuccessRemoteEvent event) {
    log.info("接收“登录成功”事件：{}", event);
  }

  @EventListener
  public void handleLogoutRemoteEvent(LogoutRemoteEvent event) {
    log.info("接收“登出”事件：{}", event);
  }
}
