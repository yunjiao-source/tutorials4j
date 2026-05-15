package tutorials4j.framework.web.core.cache;

import tutorials4j.framework.cache.core.template.AbstractCounterCacheTemplate;

/**
 * 幂等性检查的缓存模板实现。
 *
 * <p>用于确保同一请求在指定时间内只被处理一次。基于计数器缓存，最大访问次数固定为 1， 即首次计数成功后，后续相同键的计数操作将触发 {@link
 * tutorials4j.framework.common.core.exception.CounterOverflowException}， 从而实现幂等性控制。
 *
 * @author Yun Jiao
 * @see AbstractCounterCacheTemplate
 */
public class IdempotentCacheTemplate extends AbstractCounterCacheTemplate {
  /** 构造幂等性缓存模板，指定缓存名称为 "idempotent"，并设置最大计数次数为 1。 */
  public IdempotentCacheTemplate() {
    super("idempotent");
    setMaxTimes(1);
  }
}
