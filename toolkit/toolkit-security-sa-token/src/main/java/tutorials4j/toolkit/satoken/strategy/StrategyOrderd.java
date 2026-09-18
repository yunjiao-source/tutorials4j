package tutorials4j.toolkit.satoken.strategy;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface StrategyOrderd {
  int AUTH_LOGGING = 100;

  int AUTH_BLOCK_URL = 300;

  int AUTH_WHITE_URL = 500;
  int AUTH_CHECK_LOGIN = 1000;

  int BEFORE_AUTH_CORS = 100;

  int BEFORE_AUTH_OPTIONS = 500;
}
