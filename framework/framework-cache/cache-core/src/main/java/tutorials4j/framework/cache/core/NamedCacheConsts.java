package tutorials4j.framework.cache.core;

/**
 * 命名缓存常量定义。
 *
 * <p>集中定义框架内各模块使用的缓存名称常量，供创建命名缓存与按名称获取缓存时引用。
 *
 * @author Yun Jiao
 */
public interface NamedCacheConsts {
  /** 图形验证码缓存 */
  String CAPTCHA_GRAPHIC = "captcha-graphic";

  /** 行为验证码缓存 */
  String CAPTCHA_BEHAVIOR = "captcha-behavior";

  /** Web 安全幂等缓存 */
  String WEB_SECURITY_IDEMPOTENT = "web-security-idempotent";

  /** Web 安全访问限制缓存 */
  String WEB_SECURITY_ACCESS_LIMITED = "web-security-access-limited";

  /** 加密处理器缓存 */
  String CRYPTO_PROCESSOR = "crypto-processor";
}
