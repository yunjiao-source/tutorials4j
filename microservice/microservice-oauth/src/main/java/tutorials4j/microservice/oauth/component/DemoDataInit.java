package tutorials4j.microservice.oauth.component;

import cn.dev33.satoken.oauth2.consts.GrantType;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.entity.PermissionEntity;
import tutorials4j.feature.oauth.entity.RoleEntity;
import tutorials4j.feature.oauth.entity.UserEntity;
import tutorials4j.feature.oauth.model.ClientCreateModel;
import tutorials4j.feature.oauth.model.DefaultScopes;
import tutorials4j.feature.oauth.model.UserStatus;
import tutorials4j.feature.oauth.service.ClientService;
import tutorials4j.feature.oauth.service.PermissionService;
import tutorials4j.feature.oauth.service.RoleService;
import tutorials4j.feature.oauth.service.UserService;
import tutorials4j.toolkit.core.enums.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DemoDataInit implements CommandLineRunner {
  private final ClientService clientService;
  private final UserService userService;
  private final PermissionService permissionService;
  private final RoleService roleService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    if (clientService.count() <= 0) {
      this.initClientData();
    }
    if (userService.count() <= 0) {
      initUserData();
    }
  }

  private void initClientData() {
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
    client3.setAllowGrantTypes(
        List.of(
            GrantType.authorization_code,
            GrantType.implicit,
            GrantType.refresh_token,
            GrantType.password,
            GrantType.client_credentials));
    clientService.create(client3);
  }

  private void initUserData() {
    Map<String, PermissionEntity> permissions = initPermissions();
    Map<String, RoleEntity> roles = initRoles(permissions);
    initUsers(roles);
  }

  private Map<String, PermissionEntity> initPermissions() {
    List<PermissionSeed> seeds =
        List.of(
            new PermissionSeed("dashboard:view", "查看仪表盘", "dashboard", "view", "查看仪表盘"),
            new PermissionSeed("profile:read", "查看个人资料", "profile", "read", "查看个人资料"),
            new PermissionSeed("profile:update", "更新个人资料", "profile", "update", "更新个人资料"),
            new PermissionSeed("user:read", "查看用户", "user", "read", "查看用户列表与详情"),
            new PermissionSeed("user:create", "创建用户", "user", "create", "创建用户"),
            new PermissionSeed("user:update", "更新用户", "user", "update", "更新用户"),
            new PermissionSeed("user:delete", "删除用户", "user", "delete", "删除用户"),
            new PermissionSeed("user:export", "导出用户", "user", "export", "导出用户数据"),
            new PermissionSeed("user:import", "导入用户", "user", "import", "导入用户数据"),
            new PermissionSeed("user:reset_password", "重置用户密码", "user", "reset_password", "重置用户密码"),
            new PermissionSeed("user:assign_role", "分配用户角色", "user", "assign_role", "给用户分配角色"),
            new PermissionSeed("role:read", "查看角色", "role", "read", "查看角色列表与详情"),
            new PermissionSeed("role:create", "创建角色", "role", "create", "创建角色"),
            new PermissionSeed("role:update", "更新角色", "role", "update", "更新角色"),
            new PermissionSeed("role:delete", "删除角色", "role", "delete", "删除角色"),
            new PermissionSeed(
                "role:assign_permission", "分配角色权限", "role", "assign_permission", "给角色分配权限"),
            new PermissionSeed("permission:read", "查看权限", "permission", "read", "查看权限列表与详情"),
            new PermissionSeed("permission:create", "创建权限", "permission", "create", "创建权限"),
            new PermissionSeed("permission:update", "更新权限", "permission", "update", "更新权限"),
            new PermissionSeed("permission:delete", "删除权限", "permission", "delete", "删除权限"),
            new PermissionSeed("audit_log:read", "查看审计日志", "audit_log", "read", "查看审计日志"),
            new PermissionSeed("audit_log:export", "导出审计日志", "audit_log", "export", "导出审计日志"),
            new PermissionSeed("system_config:read", "查看系统配置", "system_config", "read", "查看系统配置"),
            new PermissionSeed(
                "system_config:update", "更新系统配置", "system_config", "update", "更新系统配置"),
            new PermissionSeed("report:view", "查看报表", "report", "view", "查看报表"),
            new PermissionSeed("report:export", "导出报表", "report", "export", "导出报表"));

    Map<String, PermissionEntity> result = new HashMap<>();
    for (PermissionSeed seed : seeds) {
      PermissionEntity permission =
          permissionService
              .getPermissionRepository()
              .findByCode(seed.code())
              .orElseGet(
                  () -> {
                    PermissionEntity entity = new PermissionEntity();
                    entity.setCode(seed.code());
                    entity.setName(seed.name());
                    entity.setResource(seed.resource());
                    entity.setAction(seed.action());
                    entity.setDescription(seed.description());
                    entity.setStatus(YesNoEnum.yes);
                    return permissionService.save(entity);
                  });
      result.put(seed.code(), permission);
    }
    return result;
  }

  private Map<String, RoleEntity> initRoles(Map<String, PermissionEntity> permissions) {
    Map<String, RoleEntity> roles = new HashMap<>();

    roles.put("SUPER_ADMIN", upsertRole("SUPER_ADMIN", "超级管理员", "拥有全部权限", permissions.values()));

    roles.put(
        "ADMIN",
        upsertRole(
            "ADMIN",
            "管理员",
            "管理用户、角色、权限、审计与报表",
            pick(
                permissions,
                Set.of(
                    "dashboard:view",
                    "profile:read",
                    "profile:update",
                    "user:read",
                    "user:create",
                    "user:update",
                    "user:delete",
                    "user:export",
                    "user:import",
                    "user:reset_password",
                    "user:assign_role",
                    "role:read",
                    "role:create",
                    "role:update",
                    "role:delete",
                    "role:assign_permission",
                    "permission:read",
                    "permission:create",
                    "permission:update",
                    "audit_log:read",
                    "audit_log:export",
                    "system_config:read",
                    "report:view",
                    "report:export"))));

    roles.put(
        "AUDITOR",
        upsertRole(
            "AUDITOR",
            "审计员",
            "只读审计与报表",
            pick(
                permissions,
                Set.of(
                    "dashboard:view",
                    "profile:read",
                    "profile:update",
                    "user:read",
                    "role:read",
                    "permission:read",
                    "audit_log:read",
                    "audit_log:export",
                    "report:view",
                    "report:export"))));

    roles.put(
        "OPERATOR",
        upsertRole(
            "OPERATOR",
            "运营人员",
            "用户运营与报表",
            pick(
                permissions,
                Set.of(
                    "dashboard:view",
                    "profile:read",
                    "profile:update",
                    "user:read",
                    "user:update",
                    "user:export",
                    "audit_log:read",
                    "report:view",
                    "report:export"))));

    roles.put(
        "USER",
        upsertRole(
            "USER",
            "普通用户",
            "基础访问权限",
            pick(permissions, Set.of("dashboard:view", "profile:read", "profile:update"))));

    return roles;
  }

  private void initUsers(Map<String, RoleEntity> roles) {
    upsertUser("admin", "系统管理员", "admin@example.com", "123456", Set.of(roles.get("SUPER_ADMIN")));

    upsertUser("auditor", "审计员", "auditor@example.com", "123456", Set.of(roles.get("AUDITOR")));

    upsertUser("operator", "运营人员", "operator@example.com", "123456", Set.of(roles.get("OPERATOR")));

    upsertUser("user", "普通用户", "user@example.com", "123456", Set.of(roles.get("USER")));
  }

  private Collection<PermissionEntity> pick(
      Map<String, PermissionEntity> permissions, Set<String> codes) {
    return codes.stream().map(permissions::get).filter(Objects::nonNull).toList();
  }

  private RoleEntity upsertRole(
      String code, String name, String description, Collection<PermissionEntity> permissions) {
    RoleEntity role =
        roleService
            .getRoleRepository()
            .findByCode(code)
            .orElseGet(
                () -> {
                  RoleEntity entity = new RoleEntity();
                  entity.setCode(code);
                  entity.setName(name);
                  entity.setDescription(description);
                  entity.setStatus(YesNoEnum.yes);
                  return entity;
                });

    role.setPermissions(new HashSet<>(permissions));
    return roleService.save(role);
  }

  private void upsertUser(
      String username, String nickname, String email, String rawPassword, Set<RoleEntity> roles) {
    userService
        .getUserRepository()
        .findWithRolesAndPermissionsByUsername(username)
        .ifPresentOrElse(
            existing -> {
              // 已存在则不覆盖密码和角色，避免生产环境重启后覆盖运维修改。
              // 如果角色为空，则补齐默认角色。
              if (existing.getRoles() == null || existing.getRoles().isEmpty()) {
                existing.setRoles(new HashSet<>(roles));
                userService.save(existing);
              }
            },
            () -> {
              UserEntity user = new UserEntity();
              user.setUsername(username);
              user.setNickname(nickname);
              user.setEmail(email);
              user.setPassword(passwordEncoder.encode(rawPassword));
              user.setStatus(UserStatus.active);
              user.setZoneInfo(ZoneId.of("Asia/Shanghai"));
              user.setLocaleInfo(Locale.SIMPLIFIED_CHINESE);
              user.setRoles(new HashSet<>(roles));
              userService.save(user);
            });
  }

  private record PermissionSeed(
      String code, String name, String resource, String action, String description) {}
}
