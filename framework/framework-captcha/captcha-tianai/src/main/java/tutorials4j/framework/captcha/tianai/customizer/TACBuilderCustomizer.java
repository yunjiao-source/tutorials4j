package tutorials4j.framework.captcha.tianai.customizer;

import cloud.tianai.captcha.application.TACBuilder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface TACBuilderCustomizer {
  void customiz(TACBuilder builder);
}
