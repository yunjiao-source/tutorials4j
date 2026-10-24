package tutorials4j.toolkit.xxljob.support;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface XxlJobSpringExecutorCustomizer {
  void customize(XxlJobSpringExecutor executor);
}
