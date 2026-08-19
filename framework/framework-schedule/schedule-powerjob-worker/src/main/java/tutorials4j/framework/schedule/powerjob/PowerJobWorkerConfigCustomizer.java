package tutorials4j.framework.schedule.powerjob;

import tech.powerjob.worker.common.PowerJobWorkerConfig;

/**
 * PowerJob Worker 配置定制器。
 *
 * <p>函数式接口，用于在 Worker 启动前对 {@link PowerJobWorkerConfig} 进行额外的自定义配置。
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface PowerJobWorkerConfigCustomizer {
  /**
   * 自定义 Worker 配置。
   *
   * @param confg PowerJob Worker 配置对象
   */
  void customize(PowerJobWorkerConfig confg);
}
