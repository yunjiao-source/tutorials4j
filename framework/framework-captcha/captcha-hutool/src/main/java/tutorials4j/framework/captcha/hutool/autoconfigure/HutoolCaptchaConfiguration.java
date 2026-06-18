package tutorials4j.framework.captcha.hutool.autoconfigure;

import jakarta.annotation.PostConstruct;
import java.awt.Font;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import tutorials4j.framework.captcha.hutool.builder.AbstractCaptchaBuilder;
import tutorials4j.framework.captcha.hutool.builder.CircleCaptchaBuilder;
import tutorials4j.framework.captcha.hutool.builder.GifCaptchaBuilder;
import tutorials4j.framework.captcha.hutool.builder.LineCaptchaBuilder;
import tutorials4j.framework.captcha.hutool.builder.ShearCaptchaBuilder;
import tutorials4j.framework.captcha.hutool.properties.HutoolCaptchaProperties;
import tutorials4j.framework.captcha.hutool.service.CircleCaptchaService;
import tutorials4j.framework.captcha.hutool.service.GifCaptchaService;
import tutorials4j.framework.captcha.hutool.service.LineCaptchaService;
import tutorials4j.framework.captcha.hutool.service.ShearCaptchaService;
import tutorials4j.framework.captcha.support.BehaviorCaptchaCacheTemplate;

/**
 * Hutool验证码自动配置类。
 *
 * <p>根据配置属性创建各种类型的验证码服务Bean（线段、圆圈、扭曲、GIF）。 负责验证配置参数的有效性并构建对应的Builder和服务实例。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HutoolCaptchaProperties.class)
public class HutoolCaptchaConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[CAPTCHA-HUTOOL] Hutool Captcha Configuration");
  }

  @Bean
  LineCaptchaService lineCaptchaService(
      HutoolCaptchaProperties properties,
      BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate) {
    log.trace("[CAPTCHA-HUTOOL] Line Captcha Service");

    HutoolCaptchaProperties.DrawingOptions options = properties.getLine();
    validate(options);

    LineCaptchaBuilder lcb = new LineCaptchaBuilder();
    fillBuilder(lcb, options);

    return new LineCaptchaService(behaviorCaptchaCacheTemplate, lcb);
  }

  @Bean
  CircleCaptchaService circleCaptchaService(
      HutoolCaptchaProperties properties,
      BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate) {
    log.trace("[CAPTCHA-HUTOOL] Circle Captcha Service");
    HutoolCaptchaProperties.DrawingOptions options = properties.getCircle();
    validate(options);

    CircleCaptchaBuilder ccb = new CircleCaptchaBuilder();
    fillBuilder(ccb, options);

    return new CircleCaptchaService(behaviorCaptchaCacheTemplate, ccb);
  }

  @Bean
  ShearCaptchaService sheareCaptchaService(
      HutoolCaptchaProperties properties,
      BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate) {
    log.trace("[CAPTCHA-HUTOOL] Shear Captcha Service");
    HutoolCaptchaProperties.DrawingOptions options = properties.getShear();
    validate(options);

    ShearCaptchaBuilder scb = new ShearCaptchaBuilder();
    fillBuilder(scb, options);

    return new ShearCaptchaService(behaviorCaptchaCacheTemplate, scb);
  }

  @Bean
  GifCaptchaService gifCaptchaService(
      HutoolCaptchaProperties properties,
      BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate) {
    HutoolCaptchaProperties.GifDrawingOptions options = properties.getGif();
    validate(options);
    Assert.isTrue(
        options.getQuality() >= 1 && options.getQuality() <= 20, "验证码配置属性‘quality‘值必须在[1, 20]之间");
    Assert.isTrue(options.getRepeat() >= 0, "验证码配置属性‘repeat‘值必须大于0");
    Assert.isTrue(
        options.getMinColor() >= 0 && options.getMinColor() <= 255,
        "验证码配置属性‘minColor‘值必须在[0, 255]之间");
    Assert.isTrue(
        options.getMaxColor() >= 0 && options.getMaxColor() <= 255,
        "验证码配置属性‘maxColor‘值必须在[0, 255]之间");

    GifCaptchaBuilder gcb = new GifCaptchaBuilder();
    fillBuilder(gcb, options);
    gcb.quality(options.getQuality())
        .repeat(options.getRepeat())
        .minColor(options.getMinColor())
        .maxColor(options.getMaxColor());

    return new GifCaptchaService(behaviorCaptchaCacheTemplate, gcb);
  }

  private void validate(HutoolCaptchaProperties.DrawingOptions drawing) {
    Assert.isTrue(drawing.getWidth() > 0, "验证码配置属性‘width‘值必须大于0");
    Assert.isTrue(drawing.getHeight() > 0, "验证码配置属性‘height‘值必须大于0");
    Assert.isTrue(drawing.getInterfereCount() > 0, "验证码配置属性‘interfereCount‘值必须大于0");

    Optional.ofNullable(drawing.getTransparency())
        .ifPresent(
            transparency ->
                Assert.isTrue(
                    drawing.getTransparency() >= 0 && drawing.getTransparency() <= 1,
                    "验证码配置属性‘transparency‘值必须在[0, 1]之间"));
    Optional.ofNullable(drawing.getFuzziness())
        .ifPresent(
            fuzziness ->
                Assert.isTrue(
                    drawing.getFuzziness() >= 0 && drawing.getFuzziness() <= 30,
                    "验证码配置属性‘fuzziness‘值必须在[0, 30]之间"));

    HutoolCaptchaProperties.CodeOptions code = drawing.getCode();
    Assert.isTrue(code.getLength() > 0, "验证码配置属性‘code.length‘值必须大于0");

    HutoolCaptchaProperties.FontOptions font = drawing.getFont();
    Assert.isTrue(font.getSize() > 0, "验证码配置属性‘font.size‘值必须大于0");
  }

  private void fillBuilder(
      AbstractCaptchaBuilder<?> builder, HutoolCaptchaProperties.DrawingOptions options) {
    Font font = createFont(options.getFont());

    builder
        .width(options.getWidth())
        .height(options.getHeight())
        .interfereCount(options.getInterfereCount())
        .backgroundColor(options.getBackgroundColor())
        .fuzziness(options.getFuzziness())
        .validIgnoreCase(options.getValidIgnoreCase())
        .font(font);

    HutoolCaptchaProperties.CodeOptions code = options.getCode();
    builder.generator(code.getGenerator().apply(code.getLength()));
  }

  @SuppressWarnings({"all"})
  private Font createFont(HutoolCaptchaProperties.FontOptions options) {
    return options.getName().getFont(options.getStyle().getMapping(), options.getSize());
  }
}
