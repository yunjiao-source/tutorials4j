package tutorials4j.framework.web.core.cache;

import tutorials4j.framework.cache.core.template.AbstractCounterCacheTemplate;

/**
 * 访问频率限制的缓存模板实现。
 *
 * <p>用于限制同一请求（或同一用户）在单位时间内的访问次数。基于计数器缓存， 最大访问次数可通过 {@link
 * tutorials4j.framework.web.core.annotation.AccessLimited#maxTimes()} 动态指定。 当实际访问次数达到上限后，后续计数操作将抛出
 * {@link tutorials4j.framework.common.core.exception.CounterOverflowException}。
 *
 * @author Yun Jiao
 * @see AbstractCounterCacheTemplate
 */
public class AccessLimitedCacheTemplate extends AbstractCounterCacheTemplate {
  /** 构造访问限制缓存模板，指定缓存名称为 "access_limited"。 最大次数由调用方通过 {@link #counting(String, int)} 动态传入。 */
  public AccessLimitedCacheTemplate() {
    super("access_limited");
  }
}
