package tutorials4j.framework.examples.satoken.simple;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.session.SaSessionCustomUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试专用Controller
 *
 * @author click33
 */
@RestController
@RequestMapping("/test/")
public class TestController {

  // 测试登录接口， 浏览器访问： http://localhost:8080/test/login
  @RequestMapping("login")
  public AjaxJson login(@RequestParam(defaultValue = "10001") String id) {
    List<String> result = new ArrayList<>();

    result.add("======================= 进入方法，测试登录接口 ========================= ");

    result.add("当前会话的token：" + StpUtil.getTokenValue());
    result.add("当前是否登录：" + StpUtil.isLogin());
    result.add("当前登录账号：" + StpUtil.getLoginIdDefaultNull());

    StpUtil.login(id); // 在当前会话登录此账号
    result.add("登录成功");
    result.add("当前是否登录：" + StpUtil.isLogin());
    result.add("当前登录账号：" + StpUtil.getLoginId());
    //		result.add("当前登录账号并转为int：" + StpUtil.getLoginIdAsInt());
    result.add("当前登录设备：" + StpUtil.getLoginDevice());
    //		result.add("当前token信息：" + StpUtil.getTokenInfo());

    return AjaxJson.getSuccessData(result);
  }

  // 测试退出登录 ， 浏览器访问： http://localhost:8080/test/logout
  @RequestMapping("logout")
  public AjaxJson logout() {
    StpUtil.logout();
    //		StpUtil.logoutByLoginId(10001);
    return AjaxJson.getSuccess();
  }

  // 测试角色接口， 浏览器访问： http://localhost:8080/test/testRole
  @RequestMapping("testRole")
  public AjaxJson testRole() {
    List<String> result = new ArrayList<>();
    result.add("======================= 进入方法，测试角色接口 ========================= ");

    result.add("是否具有角色标识 user " + StpUtil.hasRole("user"));
    result.add("是否具有角色标识 admin " + StpUtil.hasRole("admin"));

    result.add("没有admin权限就抛出异常");
    StpUtil.checkRole("admin");

    result.add("在【admin、user】中只要拥有一个就不会抛出异常");
    StpUtil.checkRoleOr("admin", "user");

    result.add("在【admin、user】中必须全部拥有才不会抛出异常");
    StpUtil.checkRoleAnd("admin", "user");

    result.add("角色测试通过");

    return AjaxJson.getSuccessData(result);
  }

  // 测试权限接口， 浏览器访问： http://localhost:8080/test/testJur
  @RequestMapping("testJur")
  public AjaxJson testJur() {
    List<String> result = new ArrayList<>();
    result.add("======================= 进入方法，测试权限接口 ========================= ");

    result.add("是否具有权限101" + StpUtil.hasPermission("101"));
    result.add("是否具有权限user-add" + StpUtil.hasPermission("user-add"));
    result.add("是否具有权限article-get" + StpUtil.hasPermission("article-get"));

    result.add("没有user-add权限就抛出异常");
    StpUtil.checkPermission("user-add");

    result.add("在【101、102】中只要拥有一个就不会抛出异常");
    StpUtil.checkPermissionOr("101", "102");

    result.add("在【101、102】中必须全部拥有才不会抛出异常");
    StpUtil.checkPermissionAnd("101", "102");

    result.add("权限测试通过");

    return AjaxJson.getSuccessData(result);
  }

  // 测试会话session接口， 浏览器访问： http://localhost:8080/test/session
  @RequestMapping("session")
  public AjaxJson session() throws JsonProcessingException {
    List<String> result = new ArrayList<>();
    result.add("======================= 进入方法，测试会话session接口 ========================= ");
    result.add("当前是否登录：" + StpUtil.isLogin());
    result.add("当前登录账号session的id" + StpUtil.getSession().getId());
    result.add("当前登录账号session的id" + StpUtil.getSession().getId());
    result.add("测试取值name：" + StpUtil.getSession().get("name"));
    StpUtil.getSession().set("name", new Date()); // 写入一个值
    result.add("测试取值name：" + StpUtil.getSession().get("name"));
    result.add(new ObjectMapper().writeValueAsString(StpUtil.getSession()));
    return AjaxJson.getSuccessData(result);
  }

  // 测试自定义session接口， 浏览器访问： http://localhost:8080/test/session2
  @RequestMapping("session2")
  public AjaxJson session2() {
    List<String> result = new ArrayList<>();
    result.add("======================= 进入方法，测试自定义session接口 ========================= ");
    // 自定义session就是无需登录也可以使用 的session ：比如拿用户的手机号当做 key， 来获取 session
    result.add("自定义 session的id为：" + SaSessionCustomUtil.getSessionById("1895544896").getId());
    result.add("测试取值name：" + SaSessionCustomUtil.getSessionById("1895544896").get("name"));
    SaSessionCustomUtil.getSessionById("1895544896").set("name", "张三"); // 写入值
    result.add("测试取值name：" + SaSessionCustomUtil.getSessionById("1895544896").get("name"));
    result.add("测试取值name：" + SaSessionCustomUtil.getSessionById("1895544896").get("name"));
    return AjaxJson.getSuccessData(result);
  }

  // ----------
  // 测试token专属session， 浏览器访问： http://localhost:8080/test/getTokenSession
  @RequestMapping("getTokenSession")
  public AjaxJson getTokenSession() {
    List<String> result = new ArrayList<>();
    result.add("======================= 进入方法，测试会话session接口 ========================= ");
    result.add("当前是否登录：" + StpUtil.isLogin());
    result.add("当前token专属session: " + StpUtil.getTokenSession().getId());

    result.add("测试取值name：" + StpUtil.getTokenSession().get("name"));
    StpUtil.getTokenSession().set("name", "张三"); // 写入一个值
    result.add("测试取值name：" + StpUtil.getTokenSession().get("name"));

    return AjaxJson.getSuccessData(result);
  }

  // 打印当前token信息， 浏览器访问： http://localhost:8080/test/tokenInfo
  @RequestMapping("tokenInfo")
  public AjaxJson tokenInfo() {
    List<String> result = new ArrayList<>();
    result.add("======================= 进入方法，打印当前token信息 ========================= ");
    SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
    result.add(tokenInfo.toString());
    return AjaxJson.getSuccessData(result);
  }

  // 测试注解式鉴权， 浏览器访问： http://localhost:8080/test/atCheck
  @SaCheckLogin // 注解式鉴权：当前会话必须登录才能通过
  @SaCheckRole("super-admin") // 注解式鉴权：当前会话必须具有指定角色标识才能通过
  @SaCheckPermission("user-add") // 注解式鉴权：当前会话必须具有指定权限才能通过
  @RequestMapping("atCheck")
  public AjaxJson atCheck() {
    List<String> result = new ArrayList<>();
    result.add("======================= 进入方法，测试注解鉴权接口 ========================= ");
    result.add("只有通过注解鉴权，才能进入此方法");
    //		StpUtil.checkActiveTimeout();
    //		StpUtil.updateLastActiveToNow();
    return AjaxJson.getSuccessData(result);
  }

  // 测试注解式鉴权， 浏览器访问： http://localhost:8080/test/atJurOr
  @RequestMapping("atJurOr")
  @SaCheckPermission(
      value = {"user-add", "user-all", "user-delete"},
      mode = SaMode.OR) // 注解式鉴权：只要具有其中一个权限即可通过校验
  public AjaxJson atJurOr() {
    return AjaxJson.getSuccessData("用户信息");
  }

  // [活动时间] 续签： http://localhost:8080/test/rene
  @RequestMapping("rene")
  public AjaxJson rene() {
    StpUtil.checkActiveTimeout();
    StpUtil.updateLastActiveToNow();
    return AjaxJson.getSuccess("续签成功");
  }

  // 测试踢人下线   浏览器访问： http://localhost:8080/test/kickOut
  @RequestMapping("kickOut")
  public AjaxJson kickOut() {
    // 先登录上
    StpUtil.login(10001);
    // 踢下线
    StpUtil.kickout(10001);
    // 再尝试获取
    StpUtil.getLoginId();
    // 返回
    return AjaxJson.getSuccess();
  }

  // 测试登录接口, 按照设备登录， 浏览器访问： http://localhost:8080/test/login2
  @RequestMapping("login2")
  public AjaxJson login2(
      @RequestParam(defaultValue = "10001") String id,
      @RequestParam(defaultValue = "PC") String device) {
    StpUtil.login(id, device);
    return AjaxJson.getSuccess();
  }

  // 测试身份临时切换： http://localhost:8080/test/switchTo
  @RequestMapping("switchTo")
  public AjaxJson switchTo() {
    List<String> result = new ArrayList<>();
    result.add("当前会话身份：" + StpUtil.getLoginIdDefaultNull());
    result.add("是否正在身份临时切换中: " + StpUtil.isSwitch());
    StpUtil.switchTo(
        10044,
        () -> {
          result.add("是否正在身份临时切换中: " + StpUtil.isSwitch());
          result.add("当前会话身份已被切换为：" + StpUtil.getLoginId());
        });
    result.add("是否正在身份临时切换中: " + StpUtil.isSwitch());
    return AjaxJson.getSuccessData(result);
  }

  // 测试会话治理   浏览器访问： http://localhost:8080/test/search
  @RequestMapping("search")
  public AjaxJson search() {
    List<String> result = new ArrayList<>();
    result.add("--------------");
    Ttime t = new Ttime().start();
    List<String> tokenValue = StpUtil.searchTokenValue("8feb8265f773", 0, 10, true);
    for (String v : tokenValue) {
      //			SaSession session = StpUtil.getSessionBySessionId(sid);
      result.add(v);
    }
    result.add("用时：" + t.end().toString());
    return AjaxJson.getSuccessData(result);
  }

  // 测试指定设备登录   浏览器访问： http://localhost:8080/test/loginByDevice
  @RequestMapping("loginByDevice")
  public AjaxJson loginByDevice() {
    List<String> result = new ArrayList<>();
    result.add("--------------");
    StpUtil.login(10001, "PC");
    return AjaxJson.getSuccessData(result);
  }

  // 测试   浏览器访问： http://localhost:8080/test/test
  @RequestMapping("test")
  public AjaxJson test() {
    List<String> result = new ArrayList<>();
    result.add("------------进来了");
    return AjaxJson.getSuccessData(result);
  }

  // 测试   浏览器访问： http://localhost:8080/test/test2
  @RequestMapping("test2")
  public AjaxJson test2() {
    return AjaxJson.getSuccess();
  }
}
