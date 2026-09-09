package tutorials4j.framework.examples.satoken;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录测试
 *
 * @author click33
 */
@RestController
@RequestMapping("/acc/")
public class LoginController {

  @RequestMapping("doLogin")
  public SaResult doLogin(String name, String pwd) {
    // 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对
    if ("zhang".equals(name) && "123456".equals(pwd)) {
      StpUtil.login(10001);
      return SaResult.ok("登录成功").set("loginId", 10001);
    }
    return SaResult.error("登录失败");
  }

  @RequestMapping("isLogin")
  public SaResult isLogin() {
    if (StpUtil.isLogin()) {
      return SaResult.data(StpUtil.getTokenInfo());
    }

    return SaResult.error("未登录");
  }

  @RequestMapping("tokenInfo")
  public SaResult tokenInfo() {
    return SaResult.data(StpUtil.getTokenInfo());
  }

  @RequestMapping("logout")
  public SaResult logout() {
    StpUtil.logout();
    return SaResult.ok();
  }
}
