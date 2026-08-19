package tutorials4j.framework.captcha.tianai.autoconfigure;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.TACBuilder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.captcha.support.GraphicCaptchaCacheTemplate;
import tutorials4j.framework.captcha.tianai.customizer.ImageResourceTACBuilderCustomizer;
import tutorials4j.framework.captcha.tianai.customizer.TACBuilderCustomizer;
import tutorials4j.framework.captcha.tianai.properties.TianaiCaptchaProperties;
import tutorials4j.framework.captcha.tianai.properties.TianaiOptions;
import tutorials4j.framework.captcha.tianai.service.ConcatCaptchaService;
import tutorials4j.framework.captcha.tianai.service.RotateCaptchaService;
import tutorials4j.framework.captcha.tianai.service.SliderCaptchaService;
import tutorials4j.framework.captcha.tianai.service.WordImageClickCaptchaService;
import tutorials4j.framework.captcha.tianai.support.CaptchaGenerateParamBuilder;
import tutorials4j.framework.captcha.tianai.support.CaptchaType;
import tutorials4j.framework.captcha.tianai.support.RedisCacheStore;
import tutorials4j.framework.captcha.tianai.support.SimpleImageCaptchaApplication;

/**
 * 天意验证码自动配置类。
 *
 * <p>负责初始化旋转、滑动、文字点选、拼图等验证码服务，以及缓存、控制器等组件。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(TianaiCaptchaProperties.class)
public class TianaiCaptchaConfiguration {
  /** 初始化：输出天意验证码配置已加载的跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[CAPTCHA-TIANAI] Tianai Captcha Configuration");
  }

  /**
   * 注册旋转验证码服务。
   *
   * @param imageCaptchaApplication 图形验证码应用
   * @param properties 天意验证码属性
   * @return 旋转验证码服务
   */
  @Bean
  @ConditionalOnMissingBean
  RotateCaptchaService rotateCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, TianaiCaptchaProperties properties) {
    log.trace("[CAPTCHA-TIANAI] Rotate Captcha Service");
    TianaiOptions options = properties.getRotate();
    options.merge(properties.getCommon());
    return new RotateCaptchaService(
        imageCaptchaApplication, CaptchaGenerateParamBuilder.of(options, CaptchaType.ROTATE));
  }

  /**
   * 注册滑动验证码服务。
   *
   * @param imageCaptchaApplication 图形验证码应用
   * @param properties 天意验证码属性
   * @return 滑动验证码服务
   */
  @Bean
  @ConditionalOnMissingBean
  SliderCaptchaService sliderCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, TianaiCaptchaProperties properties) {
    log.trace("[CAPTCHA-TIANAI] Slider Captcha Service");
    TianaiOptions options = properties.getSlider();
    options.merge(properties.getCommon());
    return new SliderCaptchaService(
        imageCaptchaApplication, CaptchaGenerateParamBuilder.of(options, CaptchaType.SLIDER));
  }

  /**
   * 注册文字点选验证码服务。
   *
   * @param imageCaptchaApplication 图形验证码应用
   * @param properties 天意验证码属性
   * @return 文字点选验证码服务
   */
  @Bean
  @ConditionalOnMissingBean
  WordImageClickCaptchaService wordImageClickCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, TianaiCaptchaProperties properties) {
    log.trace("[CAPTCHA-TIANAI] Word Image Click Captcha Service");
    TianaiOptions options = properties.getSlider();
    options.merge(properties.getCommon());
    return new WordImageClickCaptchaService(
        imageCaptchaApplication,
        CaptchaGenerateParamBuilder.of(options, CaptchaType.WORD_IMAGE_CLICK));
  }

  /**
   * 注册拼图验证码服务。
   *
   * @param imageCaptchaApplication 图形验证码应用
   * @param properties 天意验证码属性
   * @return 拼图验证码服务
   */
  @Bean
  @ConditionalOnMissingBean
  ConcatCaptchaService concatCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, TianaiCaptchaProperties properties) {
    log.trace("[CAPTCHA-TIANAI] Concat Captcha Service");
    TianaiOptions options = properties.getConcat();
    options.merge(properties.getCommon());
    return new ConcatCaptchaService(
        imageCaptchaApplication, CaptchaGenerateParamBuilder.of(options, CaptchaType.CONCAT));
  }

  /**
   * 注册默认的图片资源 TAC Builder 定制器。
   *
   * @return 图片资源定制器
   */
  @Bean
  @ConditionalOnMissingBean
  ImageResourceTACBuilderCustomizer defaultResourceTACBuilderCustomizer() {
    log.trace("[CAPTCHA-TIANAI] Image Resource TAC Builder Customizer");
    return new ImageResourceTACBuilderCustomizer();
  }

  /**
   * 注册基于图形验证码缓存模板的 Redis 缓存存储器。
   *
   * @param captchaCacheTemplate 图形验证码缓存模板
   * @return Redis 缓存存储器
   */
  @Bean
  @ConditionalOnMissingBean
  RedisCacheStore redisCacheStore(GraphicCaptchaCacheTemplate captchaCacheTemplate) {
    log.trace("[CAPTCHA-TIANAI] Redis Cache Store");
    return new RedisCacheStore(captchaCacheTemplate);
  }

  /**
   * 注册图形验证码应用。
   *
   * <p>构建默认模板与缓存存储的 {@link TACBuilder}，并依次应用所有定制的 {@link TACBuilderCustomizer}。
   *
   * @param redisCacheStore Redis 缓存存储器
   * @param customizers TAC Builder 定制器提供者
   * @return 图形验证码应用
   */
  @Bean
  @ConditionalOnMissingBean
  ImageCaptchaApplication imageCaptchaApplication(
      RedisCacheStore redisCacheStore, ObjectProvider<TACBuilderCustomizer> customizers) {
    log.trace("[CAPTCHA-TIANAI] Slider Image Captcha Application");
    TACBuilder builder = TACBuilder.builder().addDefaultTemplate().setCacheStore(redisCacheStore);
    customizers.orderedStream().forEach(customizer -> customizer.customiz(builder));

    return SimpleImageCaptchaApplication.of(builder.build());
  }
}
