package tutorials4j.framework.feature.schedule;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import tutorials4j.framework.schedule.core.bean.ChangeStatusEvent;
import tutorials4j.framework.schedule.core.component.ChangeStatusEventConsumer;
import tutorials4j.framework.schedule.core.component.EventConsumerContainer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class AsyncEventConsumerContainer implements EventConsumerContainer {
  private final List<ChangeStatusEventConsumer> consumers;

  @Override
  @Async
  public void notifyConsumers(ChangeStatusEvent event) {
    consumers.forEach(consumer -> consumer.consumer(event));
  }
}
