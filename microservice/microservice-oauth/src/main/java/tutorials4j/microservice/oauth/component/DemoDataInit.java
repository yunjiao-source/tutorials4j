package tutorials4j.microservice.oauth.component;

import cn.dev33.satoken.oauth2.consts.GrantType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.model.ClientCreateModel;
import tutorials4j.feature.oauth.model.DefaultScopes;
import tutorials4j.feature.oauth.service.ClientService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DemoDataInit implements CommandLineRunner {
  private final ClientService clientService;

  @Override
  public void run(String... args) throws Exception {
    if (clientService.count() <= 0) {
      ClientCreateModel client1 = new ClientCreateModel();
      client1.setClientId("1001");
      client1.setClientName("人力资源管理系统");
      client1.setClientSecret("aaaa-bbbb-cccc-dddd-eeee");
      client1.setAllowRedirectUris(List.of("*"));
      client1.setContractScopes(DefaultScopes.getAllScopes());
      client1.setSubjectId("1000001");
      client1.setAllowGrantTypes(
          List.of(
              GrantType.authorization_code, // 授权码式
              GrantType.implicit, // 隐藏式
              GrantType.refresh_token, // 刷新令牌
              GrantType.password, // 密码式
              GrantType.client_credentials, // 客户端模式
              "phone_code" // 自定义授权模式 手机号验证码登录
              ));
      clientService.create(client1);

      ClientCreateModel client2 = new ClientCreateModel();
      client2.setClientId("1002");
      client2.setClientName("财务系统");
      client2.setClientSecret("aaaa-bbbb-cccc-dddd-eeee");
      client2.setAllowRedirectUris(List.of("*"));
      client2.setContractScopes(DefaultScopes.getAllScopes());
      client2.setSubjectId("1000001");
      client2.setAllowGrantTypes(
          List.of(
              GrantType.authorization_code,
              GrantType.implicit,
              GrantType.refresh_token,
              GrantType.password,
              GrantType.client_credentials));
      clientService.create(client2);

      ClientCreateModel client3 = new ClientCreateModel();
      client3.setClientId("1003");
      client3.setClientName("Erp系统");
      client3.setClientSecret("aaaa-bbbb-cccc-dddd-eeee");
      client3.setAllowRedirectUris(List.of("*"));
      client3.setContractScopes(DefaultScopes.getAllScopes());
      client3.setContractScopes(
          List.of(
              GrantType.authorization_code,
              GrantType.implicit,
              GrantType.refresh_token,
              GrantType.password,
              GrantType.client_credentials));
      clientService.create(client3);
    }
  }
}
