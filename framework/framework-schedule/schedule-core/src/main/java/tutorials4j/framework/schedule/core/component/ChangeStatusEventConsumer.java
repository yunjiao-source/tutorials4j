package tutorials4j.framework.schedule.core.component;

import tutorials4j.framework.schedule.core.bean.ChangeStatusEvent;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface ChangeStatusEventConsumer {
  void consumer(ChangeStatusEvent event);
}
