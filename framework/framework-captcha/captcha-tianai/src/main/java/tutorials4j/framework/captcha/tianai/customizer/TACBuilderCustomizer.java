package tutorials4j.framework.captcha.tianai.customizer;

import cloud.tianai.captcha.application.TACBuilder;

/**
 * TAC构建器定制器函数式接口。
 *
 * <p>用于对 {@link TACBuilder} 进行自定义配置。
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface TACBuilderCustomizer {

  /**
   * 定制 TAC 构建器。
   *
   * @param builder 待定制的 TAC 构建器
   */
  void customiz(TACBuilder builder);
}
