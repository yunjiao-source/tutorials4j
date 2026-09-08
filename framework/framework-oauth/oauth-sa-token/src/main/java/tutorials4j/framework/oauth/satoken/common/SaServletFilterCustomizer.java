package tutorials4j.framework.oauth.satoken.common;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

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

  default void backSsoLogin(Object o, String ssoLoginUrl) {
    if (!StpUtil.isLogin() && StringUtils.isNotBlank(ssoLoginUrl)) {
      String back =
          SaFoxUtil.joinParam(
              SaHolder.getRequest().getUrl(), SpringMVCUtil.getRequest().getQueryString());
      SaHolder.getResponse().redirect(ssoLoginUrl + "?back=" + SaFoxUtil.encodeUrl(back));
      SaRouter.back();
    }
  }

  default void back401Code(Object o) {
    if (!StpUtil.isLogin()) {
      // 与前端约定好，code=401时代表会话未登录
      SaRouter.back(SaResult.ok().setCode(401));
    }
  }

  default SaServletFilterCustomizer andThen(SaServletFilterCustomizer after) {
    Objects.requireNonNull(after);
    return (filter) -> {
      customize(filter);
      after.customize(filter);
    };
  }
}
