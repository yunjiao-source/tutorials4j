package tutorials4j.framework.captcha.support;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.apache.commons.lang3.EnumUtils;
import tutorials4j.framework.captcha.exception.CaptchaErrorCode;

/**
 * 验证码服务工厂。
 *
 * <p>持有各验证码类别对应的服务实现，支持按类别名称或类别枚举查找服务实例。
 *
 * @author Yun Jiao
 */
public class CaptchaServiceFactory {
  /** 单例实例。 */
  public static final CaptchaServiceFactory instance = new CaptchaServiceFactory();

  /** 验证码类别到服务实现的映射。 */
  protected EnumMap<CaptchaCategory, CaptchaService> services =
      new EnumMap<>(CaptchaCategory.class);

  /**
   * 根据分类代码，查找验证码服务
   *
   * @param categoryName 分类名称，必须值
   * @return 实例
   */
  public CaptchaService findService(String categoryName) {
    CaptchaCategory category = EnumUtils.getEnum(CaptchaCategory.class, categoryName);
    if (category == null) {
      throw CaptchaErrorCode.CAPTCHA_CATEGORY_NOT_EXISTS.throwed().param("category", categoryName);
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
      throw CaptchaErrorCode.CAPTCHA_SERVICE_NOT_EXISTS.throwed().param("category", category);
    }

    return service;
  }

  /**
   * 返回所有验证码服务实例的不可变视图。
   *
   * @return 验证码类别到服务实现的映射
   */
  public Map<CaptchaCategory, CaptchaService> getServices() {
    return Collections.unmodifiableMap(services);
  }

  /**
   * 注册验证码服务实现。
   *
   * @param services 验证码类别到服务实现的映射
   */
  public void setServices(Map<CaptchaCategory, CaptchaService> services) {
    this.services.putAll(services);
  }
}
