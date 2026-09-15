package tutorials4j.toolkit.oauth.satoken;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.stp.StpUtil;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface SaServletFilterCustomizer {
  void customize(SaServletFilter filter);

  default void logger(Object o) {
    SaManager.getLog()
        .debug(
            "请求path={}  提交token={}",
            SaHolder.getRequest().getRequestPath(),
            StpUtil.getTokenValue());
  }
}
