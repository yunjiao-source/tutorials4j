package tutorials4j.framework.schedule.powerjob;

import tech.powerjob.worker.common.PowerJobWorkerConfig;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface PowerJobWorkerConfigCustomizer {
  void customize(PowerJobWorkerConfig confg);
}
