package tutorials4j.feature.bus.event.remote;

import tutorials4j.feature.core.event.local.AuthEvent;
import tutorials4j.toolkit.bus.event.BaseRemoteEvent;
import tutorials4j.toolkit.core.util.SecurityUtils;
import tutorials4j.toolkit.json.util.JacksonUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class AuthRemoteEvent extends BaseRemoteEvent {

  public AuthRemoteEvent() {
    super();
  }

  public AuthRemoteEvent(Object source, String originService, AuthEvent event) {
    super(new Object(), originService, DEFAULT_DESTINATION_FACTORY.getDestination(null));
    setData(JacksonUtils.instance.toJson(event));
    setPublisher(SecurityUtils.getAccount());
  }
}
