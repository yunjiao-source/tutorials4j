package tutorials4j.feature.oauth.event.remote;

import tutorials4j.feature.oauth.event.AuthEvent;
import tutorials4j.toolkit.bus.event.BaseRemoteEvent;
import tutorials4j.toolkit.core.util.SecurityUtils;
import tutorials4j.toolkit.json.util.JacksonUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class LoginFailRemoteEvent extends BaseRemoteEvent {

  public LoginFailRemoteEvent() {
    super();
  }

  public LoginFailRemoteEvent(Object source, String originService, AuthEvent event) {
    super(new Object(), originService, DEFAULT_DESTINATION_FACTORY.getDestination(null));
    setData(JacksonUtils.instance.toJson(event));
    setPublisher(SecurityUtils.getAccount());
  }
}
