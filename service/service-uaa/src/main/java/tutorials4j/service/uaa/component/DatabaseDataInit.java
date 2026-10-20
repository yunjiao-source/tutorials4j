package tutorials4j.service.uaa.component;

import cn.dev33.satoken.oauth2.consts.GrantType;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.feature.upms.entity.UserEntity;
import tutorials4j.feature.upms.model.ClientCreateDTO;
import tutorials4j.feature.upms.model.DefaultScopes;
import tutorials4j.feature.upms.model.RoleCreateDTO;
import tutorials4j.feature.upms.model.UserCreateDTO;
import tutorials4j.feature.upms.service.ClientService;
import tutorials4j.feature.upms.service.RoleService;
import tutorials4j.feature.upms.service.UserService;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Component
@RequiredArgsConstructor
public class DatabaseDataInit implements CommandLineRunner {
  private final ClientService clientService;
  private final UserService userService;
  private final RoleService roleService;

  @Override
  public void run(String... args) throws Exception {
    initClient();
    initUser();
  }

  private void initClient() {
    if (clientService.count() <= 0) {
      ClientCreateDTO client1 = new ClientCreateDTO();
      client1.setClientId("1001");
      client1.setClientName("vue-vben-admin系统");
      client1.setClientSecret("aaaa-bbbb-cccc-dddd-eeee");
      client1.setAllowRedirectUris(List.of("*"));
      client1.setContractScopes(
          List.of(
              DefaultScopes.profile.name(),
              DefaultScopes.email.name(),
              DefaultScopes.phone.name()));
      client1.setSubjectId("1000001");
      client1.setAllowGrantTypes(
          List.of(
              GrantType.authorization_code, // 授权码式
              GrantType.implicit, // 隐藏式
              GrantType.refresh_token, // 刷新令牌
              GrantType.password, // 密码式
              GrantType.client_credentials // 客户端模式
              ));
      client1.setIsReserved(YesNoEnum.yes);
      clientService.create(client1);

      ClientCreateDTO client2 = new ClientCreateDTO();
      client2.setClientId("1002");
      client2.setClientName("DEMO系统");
      client2.setClientSecret("aaaa-bbbb-cccc-dddd-eeee");
      client2.setAllowRedirectUris(List.of("*"));
      client2.setContractScopes(DefaultScopes.getAllScopes());
      client2.setSubjectId("1000001");
      client2.setAllowGrantTypes(
          List.of(
              GrantType.authorization_code, // 授权码式
              GrantType.implicit, // 隐藏式
              GrantType.refresh_token, // 刷新令牌
              GrantType.password, // 密码式
              GrantType.client_credentials // 客户端模式
              ));
      clientService.create(client2);
    }
  }

  private void initUser() {
    RoleEntity role = roleService.getRoleRepository().findByCode("admin").orElse(null);
    if (role == null) {
      RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
      roleCreateDTO.setCode("admin");
      roleCreateDTO.setName("管理员");
      roleCreateDTO.setDescription("包含所有权限");
      roleCreateDTO.setIsReserved(YesNoEnum.yes);
      role = roleService.create(roleCreateDTO);
    }

    UserEntity user = userService.getUserRepository().findByUsername("admin").orElse(null);
    if (user == null) {
      UserCreateDTO userCreateDTO = new UserCreateDTO();
      userCreateDTO.setUsername("admin");
      userCreateDTO.setName("系统管理员");
      userCreateDTO.setEmail("admin@example.com");
      userCreateDTO.setPassword("123456");
      userCreateDTO.setIsReserved(YesNoEnum.yes);
      user = userService.create(userCreateDTO);

      user.setRoles(Set.of(role));
      userService.save(user);
    }
  }
}
