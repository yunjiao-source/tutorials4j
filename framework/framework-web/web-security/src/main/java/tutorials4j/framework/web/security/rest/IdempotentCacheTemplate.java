package tutorials4j.framework.web.security.rest;

import tutorials4j.framework.cache.core.NamedCacheConsts;
import tutorials4j.framework.cache.core.template.AbstractCounterCacheTemplate;

/**
 * 幂等性检查的缓存模板实现。
 *
 * <p>用于确保同一键（如同一请求标识）在指定时间内只被处理一次。基于计数器缓存，最大访问次数固定为 1， 即首次计数成功后，后续相同键的计数操作将触发 {@link
 * tutorials4j.framework.cache.core.exception.CounterOverflowException}，从而实现幂等性控制。
 *
 * @author Yun Jiao
 * @see AbstractCounterCacheTemplate
 */
public class IdempotentCacheTemplate extends AbstractCounterCacheTemplate {
  /**
   * 构造幂等性缓存模板，缓存名称固定为 {@code web-security-idempotent} （对应 {@link
   * tutorials4j.framework.cache.core.NamedCacheConsts#WEB_SECURITY_IDEMPOTENT}）， 并将最大计数次数设置为 1。
   */
  public IdempotentCacheTemplate() {
    super(NamedCacheConsts.WEB_SECURITY_IDEMPOTENT);
    setMaxTimes(1);
  }
}
