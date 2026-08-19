package tutorials4j.framework.feature.signin.service;

/**
 * 签到结果处理器
 *
 * <p>用于在签到完成后对签到结果进行后续处理（如积分、通知等扩展逻辑）。 实现类可通过 {@link #sourceSupported()} 指定支持的签到来源，或通过 {@link
 * #allSupported()} 声明支持所有来源。
 *
 * @author Yun Jiao
 */
public interface SignInResultHandler {
  /** 处理签到结果 */
  void handle(SignInResult result);

  /** 返回该处理器支持的签到来源标识 */
  String sourceSupported();

  /** 是否支持所有签到来源 */
  boolean allSupported();
}
