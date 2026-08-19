package tutorials4j.framework.cache.core.properties;

import lombok.Data;

/**
 * 本地锁配置选项。
 *
 * <p>用于配置基于内存的本地锁（如 striped lock）的相关参数。
 *
 * @author Yun Jiao
 */
@Data
public class LocalLockOptions {
  /** 锁分片数量，默认 128。分片越多，锁竞争粒度越细，但占用内存也越多。 */
  private Integer stripes = 128;
}
