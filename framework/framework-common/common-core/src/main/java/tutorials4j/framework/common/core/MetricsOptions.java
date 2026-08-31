package tutorials4j.framework.common.core;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 对象存储指标配置选项。
 *
 * <p>用于控制 Micrometer 指标统计的行为，如是否启用、全局标签、百分位计算等。
 *
 * @author Yun Jiao
 */
@Data
public class MetricsOptions {
  /** 是否启用指标统计，默认 true */
  private boolean enabled = true;

  /** 额外全局标签，如 application=myapp，会附加到所有指标上 */
  private List<String> tags = new ArrayList<>();

  /** 百分位值，默认 50、95、99 分位，对应 p50、p95、p99 */
  private double[] percentiles = {0.5, 0.95, 0.99};
}
