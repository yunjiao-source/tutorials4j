package tutorials4j.feature.oauth.event.remote;

import tutorials4j.feature.oauth.event.UserSessionEvent;
import tutorials4j.toolkit.bus.event.BaseRemoteEvent;
import tutorials4j.toolkit.core.util.SecurityUtils;
import tutorials4j.toolkit.json.util.JacksonUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class LogoutRemoteEvent extends BaseRemoteEvent {

  public LogoutRemoteEvent() {
    super();
  }

  public LogoutRemoteEvent(Object source, String originService, UserSessionEvent event) {
    super(new Object(), originService, DEFAULT_DESTINATION_FACTORY.getDestination(null));
    setData(JacksonUtils.instance.toJson(event));
    setPublisher(SecurityUtils.getAccount());
  }
}
