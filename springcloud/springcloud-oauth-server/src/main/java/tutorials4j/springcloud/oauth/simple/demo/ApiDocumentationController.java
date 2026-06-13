// ApiDocumentationController.java
package tutorials4j.springcloud.oauth.simple.demo;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiDocumentationController {

  @GetMapping("/endpoints")
  public List<EndpointInfo> getEndpoints() {
    List<EndpointInfo> endpoints = new ArrayList<>();

    // OAuth2 核心端点
    endpoints.add(new EndpointInfo("GET", "/oauth2/authorize", "授权端点，发起授权码流程", "需用户登录"));
    endpoints.add(
        new EndpointInfo(
            "POST",
            "/oauth2/token",
            "令牌端点，支持 authorization_code, refresh_token, client_credentials",
            "需 Client 认证"));
    endpoints.add(new EndpointInfo("GET", "/oauth2/jwks", "JWK 公钥集端点，用于客户端校验 JWT", "无需认证"));
    endpoints.add(
        new EndpointInfo(
            "GET",
            "/userinfo",
            "OIDC 用户信息端点，需在 Authorization 头携带 Bearer Token（Access Token）",
            "Bearer Token (Access Token)"));
    endpoints.add(new EndpointInfo("GET", "/oauth2/consent", "授权确认页面 (用户同意页面)", "需用户登录"));

    // 登录 / 登出
    endpoints.add(new EndpointInfo("GET", "/login", "登录页面", "无需认证"));
    endpoints.add(new EndpointInfo("POST", "/login", "提交登录表单", "无需认证"));
    endpoints.add(new EndpointInfo("GET", "/logout", "退出登录页面", "需用户登录"));
    endpoints.add(new EndpointInfo("POST", "/logout", "执行退出", "需用户登录"));

    // Actuator 端点 (部分公开，部分需 OPS 角色)
    endpoints.add(new EndpointInfo("GET", "/actuator/health", "健康检查", "公开"));
    endpoints.add(new EndpointInfo("GET", "/actuator/prometheus", "Prometheus 指标", "公开"));
    endpoints.add(new EndpointInfo("GET", "/actuator/info", "应用信息", "需角色 OPS"));
    endpoints.add(new EndpointInfo("GET", "/actuator", "Actuator 根路径", "需角色 OPS"));

    // 文档自身接口
    endpoints.add(new EndpointInfo("GET", "/api/endpoints", "获取接口文档数据 (本接口)", "公开"));
    endpoints.add(new EndpointInfo("GET", "/", "接口文档首页 (本页面)", "公开"));

    return endpoints;
  }

  static class EndpointInfo {
    private String method;
    private String path;
    private String description;
    private String auth;

    public EndpointInfo(String method, String path, String description, String auth) {
      this.method = method;
      this.path = path;
      this.description = description;
      this.auth = auth;
    }

    // getters (Jackson 需要)
    public String getMethod() {
      return method;
    }

    public String getPath() {
      return path;
    }

    public String getDescription() {
      return description;
    }

    public String getAuth() {
      return auth;
    }
  }
}
