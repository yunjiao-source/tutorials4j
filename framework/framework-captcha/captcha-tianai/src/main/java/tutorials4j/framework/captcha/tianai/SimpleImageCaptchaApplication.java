package tutorials4j.framework.captcha.tianai;

import cloud.tianai.captcha.application.DefaultImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaProperties;
import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class SimpleImageCaptchaApplication extends DefaultImageCaptchaApplication {

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

  @Override
  protected String getKey(String id) {
    return id;
  }
}
