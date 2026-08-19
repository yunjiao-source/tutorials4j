package tutorials4j.framework.captcha.tianai.customizer;

import cloud.tianai.captcha.application.TACBuilder;

/**
 * TAC 构建器定制器函数式接口。
 *
 * <p>用于对 {@link TACBuilder} 进行自定义配置，以满足不同的验证码构建需求。
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface TACBuilderCustomizer {

  /**
   * 对 TAC 构建器执行自定义配置。
   *
   * @param builder 待定制的 TAC 构建器
   */
  void customiz(TACBuilder builder);
}
