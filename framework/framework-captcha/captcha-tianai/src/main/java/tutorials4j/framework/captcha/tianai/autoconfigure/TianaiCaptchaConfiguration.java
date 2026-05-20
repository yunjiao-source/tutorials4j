package tutorials4j.framework.captcha.tianai.autoconfigure;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.TACBuilder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.captcha.CaptchaServiceFactory;
import tutorials4j.framework.captcha.GraphicCaptchaCacheTemplate;
import tutorials4j.framework.captcha.properties.TianaiCaptchaProperties;
import tutorials4j.framework.captcha.properties.TianaiOptions;
import tutorials4j.framework.captcha.tianai.CaptchaType;
import tutorials4j.framework.captcha.tianai.RedisCacheStore;
import tutorials4j.framework.captcha.tianai.SimpleImageCaptchaApplication;
import tutorials4j.framework.captcha.tianai.TianAiCaptchaGenerateParamBuilder;
import tutorials4j.framework.captcha.tianai.customizer.ImageResourceTACBuilderCustomizer;
import tutorials4j.framework.captcha.tianai.customizer.TACBuilderCustomizer;
import tutorials4j.framework.captcha.tianai.service.ConcatCaptchaService;
import tutorials4j.framework.captcha.tianai.service.RotateCaptchaService;
import tutorials4j.framework.captcha.tianai.service.SliderCaptchaService;
import tutorials4j.framework.captcha.tianai.service.WordImageClickCaptchaService;
import tutorials4j.framework.captcha.tianai.web.TianaiCaptchaController;

/**
 * 天意验证码自动配置类。
 *
 * <p>负责初始化旋转、滑动、文字点选、拼图等验证码服务，以及缓存、控制器等组件。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class TianaiCaptchaConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[CAPTCHA-TIANAI] Tianai Captcha Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  RotateCaptchaService rotateCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, TianaiCaptchaProperties properties) {
    log.debug("[CAPTCHA-TIANAI] Rotate Captcha Service");
    TianaiOptions options = properties.getRotate();
    options.merge(properties.getCommon());
    return new RotateCaptchaService(
        imageCaptchaApplication, TianAiCaptchaGenerateParamBuilder.of(options, CaptchaType.ROTATE));
  }

  @Bean
  @ConditionalOnMissingBean
  SliderCaptchaService sliderCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, TianaiCaptchaProperties properties) {
    log.debug("[CAPTCHA-TIANAI] Slider Captcha Service");
    TianaiOptions options = properties.getSlider();
    options.merge(properties.getCommon());
    return new SliderCaptchaService(
        imageCaptchaApplication, TianAiCaptchaGenerateParamBuilder.of(options, CaptchaType.SLIDER));
  }

  @Bean
  @ConditionalOnMissingBean
  WordImageClickCaptchaService wordImageClickCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, TianaiCaptchaProperties properties) {
    log.debug("[CAPTCHA-TIANAI] Word Image Click Captcha Service");
    TianaiOptions options = properties.getSlider();
    options.merge(properties.getCommon());
    return new WordImageClickCaptchaService(
        imageCaptchaApplication,
        TianAiCaptchaGenerateParamBuilder.of(options, CaptchaType.WORD_IMAGE_CLICK));
  }

  @Bean
  @ConditionalOnMissingBean
  ConcatCaptchaService concatCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, TianaiCaptchaProperties properties) {
    log.debug("[CAPTCHA-TIANAI] Concat Captcha Service");
    TianaiOptions options = properties.getConcat();
    options.merge(properties.getCommon());
    return new ConcatCaptchaService(
        imageCaptchaApplication,
        TianAiCaptchaGenerateParamBuilder.of(options, CaptchaType.WORD_IMAGE_CLICK));
  }

  @Bean
  @ConditionalOnMissingBean
  ImageResourceTACBuilderCustomizer defaultResourceTACBuilderCustomizer() {
    log.debug("[CAPTCHA-TIANAI] Image Resource TAC Builder Customizer");
    return new ImageResourceTACBuilderCustomizer();
  }

  @Bean
  @ConditionalOnMissingBean
  RedisCacheStore redisCacheStore(GraphicCaptchaCacheTemplate captchaCacheTemplate) {
    log.debug("[CAPTCHA-TIANAI] Redis Cache Store");
    return new RedisCacheStore(captchaCacheTemplate);
  }

  @Bean(destroyMethod = "close")
  @ConditionalOnMissingBean
  ImageCaptchaApplication imageCaptchaApplication(
      RedisCacheStore redisCacheStore, ObjectProvider<TACBuilderCustomizer> customizers) {
    log.debug("[CAPTCHA-TIANAI] Slider Image Captcha Application");
    TACBuilder builder = TACBuilder.builder().addDefaultTemplate().setCacheStore(redisCacheStore);
    customizers.orderedStream().forEach(customizer -> customizer.customiz(builder));

    return SimpleImageCaptchaApplication.of(builder.build());
  }

  @Bean
  @ConditionalOnMissingBean
  TianaiCaptchaController tianaiCaptchaController(
      CaptchaServiceFactory factory, ImageCaptchaApplication imageCaptchaApplication) {
    log.debug("[CAPTCHA-TIANAI] Tianai Captcha Controller");
    return new TianaiCaptchaController(factory, imageCaptchaApplication);
  }
}
