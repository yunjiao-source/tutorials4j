package tutorials4j.framework.schedule.core.component;

import java.util.List;
import lombok.RequiredArgsConstructor;
import tutorials4j.framework.schedule.core.bean.ChangeStatusEvent;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class SyncEventConsumerContainer implements EventConsumerContainer {
  private final List<ChangeStatusEventConsumer> consumers;

  @Override
  public void notifyConsumers(ChangeStatusEvent event) {
    consumers.forEach(consumer -> consumer.consumer(event));
  }
}
