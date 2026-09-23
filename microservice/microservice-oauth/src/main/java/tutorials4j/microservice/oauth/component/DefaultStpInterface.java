package tutorials4j.microservice.oauth.component;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.entity.PermissionEntity;
import tutorials4j.feature.oauth.entity.RoleEntity;
import tutorials4j.feature.oauth.service.PermissionService;
import tutorials4j.feature.oauth.service.RoleService;
import tutorials4j.feature.oauth.service.UserService;
import tutorials4j.toolkit.satoken.autoconfigure.SaTokenSecurityToolkitProperties;
import tutorials4j.toolkit.satoken.component.ToolkitStpInterface;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
public class DefaultStpInterface extends ToolkitStpInterface {
  private final UserService userService;
  private final RoleService roleService;
  private final PermissionService permissionService;
  public DefaultStpInterface(
      SaTokenSecurityToolkitProperties properties, UserService userService, RoleService roleService,
      PermissionService permissionService) {
    super(properties.getPermission());
    this.userService = userService;
    this.roleService = roleService;
    this.permissionService = permissionService;
  }

  @Override
  protected List<String> getAllPermissions() {
    return permissionService.findAll()
        .stream()
        .map(PermissionEntity::getCode)
        .toList();
  }

  @Override
  protected List<String> getPermissionsByRole(String role) {
    return roleService
        .getRoleRepository()
        .findWithPermissionByCode(role)
        .map(r -> r.getPermissions().stream().map(PermissionEntity::getCode).toList())
        .orElse(Collections.emptyList());
  }

  @Override
  protected List<String> getRoleByUsername(String username) {
    return userService
        .getUserRepository()
        .findWithRolesByUsername(username)
        .map(u -> u.getRoles().stream().map(RoleEntity::getCode).toList())
        .orElse(Collections.emptyList());
  }
}
