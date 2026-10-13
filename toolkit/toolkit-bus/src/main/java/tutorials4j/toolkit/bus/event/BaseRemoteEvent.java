package tutorials4j.toolkit.bus.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.bus.event.Destination;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.core.style.ToStringCreator;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
public class BaseRemoteEvent extends RemoteApplicationEvent {
  private String data;
  private String publisher;

  public BaseRemoteEvent() {}

  public BaseRemoteEvent(Object source, String originService, Destination destination) {
    super(source, originService, destination);
  }

  @Override
  public String toString() {
    return new ToStringCreator(this)
        .append("id", getId())
        .append("originService", getOriginService())
        .append("destinationService", getDestinationService())
        .append("publisher", getPublisher())
        .append("data", getData())
        .toString();
  }
}
