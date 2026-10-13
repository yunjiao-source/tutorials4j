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
public class LoginSuccessRemoteEvent extends BaseRemoteEvent {

  public LoginSuccessRemoteEvent() {
    super();
  }

  public LoginSuccessRemoteEvent(Object source, String originService, UserSessionEvent event) {
    // https://github.com/spring-cloud/spring-cloud-stream/issues/3162
    // super(source, originService, DEFAULT_DESTINATION_FACTORY.getDestination(null));
    super(new Object(), originService, DEFAULT_DESTINATION_FACTORY.getDestination(null));
    setData(JacksonUtils.instance.toJson(event));
    setPublisher(SecurityUtils.getAccount());
  }
}
