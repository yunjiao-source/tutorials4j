package tutorials4j.framework.captcha.support;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.apache.commons.lang3.EnumUtils;
import tutorials4j.framework.captcha.exception.CaptchaErrorCode;

/**
 * 验证码服务工厂
 *
 * @author Yun Jiao
 */
public class CaptchaServiceFactory {
  public static final CaptchaServiceFactory instance = new CaptchaServiceFactory();

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

  public Map<CaptchaCategory, CaptchaService> getServices() {
    return Collections.unmodifiableMap(services);
  }

  public void setServices(Map<CaptchaCategory, CaptchaService> services) {
    this.services.putAll(services);
  }
}
