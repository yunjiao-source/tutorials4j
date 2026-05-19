package tutorials4j.framework.captcha;

import java.util.Map;
import tutorials4j.framework.captcha.exception.CaptchaException;
import tutorials4j.framework.common.core.support.EnumCache;

/**
 * 验证码服务工厂
 *
 * @author Yun Jiao
 */
public record CaptchaServiceFactory(Map<CaptchaCategory, CaptchaService> services) {

  /**
   * 根据分类代码，查找验证码服务
   *
   * @param categoryName 分类名称，必须值
   * @return 实例
   */
  public CaptchaService findService(String categoryName) {
    CaptchaCategory category = EnumCache.findByName(CaptchaCategory.class, categoryName);
    if (category == null) {
      throw new CaptchaException("验证码分类代码不存在，名称是：" + categoryName);
    }
    return findService(category);
  }

  /**
   * 根据分类，查找验证码服务
   *
   * @param category 分类
   * @return 实例
   */
  public CaptchaService findService(CaptchaCategory category) {
    CaptchaService service = services.get(category);
    if (service == null) {
      throw new CaptchaException("根据分类查找验证码服务未找到, 分类是：" + category);
    }

    return service;
  }
}
