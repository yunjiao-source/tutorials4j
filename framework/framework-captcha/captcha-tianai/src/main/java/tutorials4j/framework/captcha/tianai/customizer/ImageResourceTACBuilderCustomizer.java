package tutorials4j.framework.captcha.tianai.customizer;

import cloud.tianai.captcha.application.TACBuilder;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import tutorials4j.framework.captcha.tianai.CaptchaType;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ImageResourceTACBuilderCustomizer implements TACBuilderCustomizer {

  @Override
  public void customiz(TACBuilder builder) {
    for (CaptchaType captchaType : CaptchaType.values()) {
      builder.addResource(
          captchaType.name(), new Resource("classpath", "META-INF/cut-image/resource/1.jpg"));
      builder.addResource(
          captchaType.name(), new Resource("classpath", "META-INF/tianai-image/resource/0.jpg"));
    }
  }
}
