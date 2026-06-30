package tutorials4j.framework.feature.signin.service;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface SignInResultHandler {
  void handle(SignInResult result);

  String sourceSupported();

  boolean allSupported();
}
