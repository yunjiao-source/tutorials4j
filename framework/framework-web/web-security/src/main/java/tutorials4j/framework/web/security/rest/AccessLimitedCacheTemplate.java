package tutorials4j.framework.web.security.rest;

import tutorials4j.framework.cache.core.NamedCacheConsts;
import tutorials4j.framework.cache.core.template.AbstractCounterCacheTemplate;
import tutorials4j.framework.web.core.annotation.AccessLimited;

/**
 * 访问频率限制的缓存模板实现。
 *
 * <p>用于限制同一键（如请求标识或用户标识）在单位时间内的访问次数。基于计数器缓存， 最大允许访问次数不固定， 由调用方在计数时动态指定（通常取自 {@link
 * AccessLimited#maxTimes()}）。 当实际计数达到上限后，后续计数操作将抛出 {@link
 * tutorials4j.framework.cache.core.exception.CounterOverflowException}。
 *
 * @author Yun Jiao
 * @see AbstractCounterCacheTemplate
 */
public class AccessLimitedCacheTemplate extends AbstractCounterCacheTemplate {
  /**
   * 构造访问限制缓存模板，缓存名称固定为 {@code web-security-access-limited} （对应 {@link
   * tutorials4j.framework.cache.core.NamedCacheConsts#WEB_SECURITY_ACCESS_LIMITED}）， 最大允许次数由调用方通过
   * {@link #counting(String, int)} 动态传入。
   */
  public AccessLimitedCacheTemplate() {
    super(NamedCacheConsts.WEB_SECURITY_ACCESS_LIMITED);
  }
}
