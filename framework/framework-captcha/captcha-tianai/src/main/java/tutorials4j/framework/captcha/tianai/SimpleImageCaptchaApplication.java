package tutorials4j.framework.captcha.tianai;

import cloud.tianai.captcha.application.DefaultImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaProperties;
import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;

/**
 * 简化版图片验证码应用，重写缓存 key 的生成逻辑。
 *
 * @author Yun Jiao
 */
public class SimpleImageCaptchaApplication extends DefaultImageCaptchaApplication {

  /**
   * 从已有的 ImageCaptchaApplication 创建 SimpleImageCaptchaApplication 实例。
   *
   * @param application 已有的图片验证码应用
   * @return 简化版应用实例
   */
  public static SimpleImageCaptchaApplication of(ImageCaptchaApplication application) {
    return new SimpleImageCaptchaApplication(
        application.getImageCaptchaGenerator(),
        application.getImageCaptchaValidator(),
        application.getCacheStore(),
        new ImageCaptchaProperties(),
        application.getCaptchaInterceptor());
  }

  public SimpleImageCaptchaApplication(
      ImageCaptchaGenerator captchaGenerator,
      ImageCaptchaValidator imageCaptchaValidator,
      CacheStore cacheStore,
      ImageCaptchaProperties prop,
      CaptchaInterceptor captchaInterceptor) {
    super(captchaGenerator, imageCaptchaValidator, cacheStore, prop, captchaInterceptor);
  }

  /**
   * 直接返回原始 id 作为缓存 key。
   *
   * @param id 验证码 id
   * @return id 本身
   */
  @Override
  protected String getKey(String id) {
    return id;
  }

  @Override
  public void close() {
    super.close();
  }
}
