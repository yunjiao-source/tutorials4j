package tutorials4j.service.test.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tutorials4j.feature.bus.event.remote.AuthRemoteEvent;
import tutorials4j.feature.core.event.AuthEvent;
import tutorials4j.toolkit.json.util.JacksonUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@Component
public class Test1Component {
  @EventListener
  public void handleLoginFailRemoteEvent(AuthRemoteEvent authRemoteEvent) {
    AuthEvent event = JacksonUtils.instance.toObject(authRemoteEvent.getData(), AuthEvent.class);
    log.info("接收'{}'事件：{}", event.getStatus(), event);
  }
}
