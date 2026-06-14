package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.schedule.core.bean.ChangeStatusEvent;
import tutorials4j.framework.schedule.core.component.ChangeStatusEventConsumer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class SimpleChangeStatusEventConsumer implements ChangeStatusEventConsumer {

  @Override
  public void consumer(ChangeStatusEvent event) {
    log.info(">>>{}", event);
  }
}
