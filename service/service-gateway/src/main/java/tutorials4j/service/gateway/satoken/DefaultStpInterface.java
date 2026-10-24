package tutorials4j.service.gateway.satoken;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import tutorials4j.feature.upms.entity.PermissionEntity;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.feature.upms.service.PermissionService;
import tutorials4j.feature.upms.service.RoleService;
import tutorials4j.feature.upms.service.UserService;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.satoken.autoconfigure.SaTokenSecurityToolkitProperties;
import tutorials4j.toolkit.satoken.support.ToolkitStpInterface;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Component
public class DefaultStpInterface extends ToolkitStpInterface {
  private final UserService userService;
  private final RoleService roleService;
  private final PermissionService permissionService;

  public DefaultStpInterface(
      SaTokenSecurityToolkitProperties properties,
      UserService userService,
      RoleService roleService,
      PermissionService permissionService) {
    super(properties.getPermission());
    this.userService = userService;
    this.roleService = roleService;
    this.permissionService = permissionService;
  }

  @Override
  protected List<String> getAllPermissions() {
    return permissionService.findAll().stream()
        .filter(e -> Objects.equals(e.getEnabled(), YesNoEnum.yes))
        .map(PermissionEntity::getCode)
        .toList();
  }

  @Override
  protected List<String> getPermissionsByRole(String role) {
    return roleService
        .getRoleRepository()
        .findWithPermissionByCodeAAndEnabled(role, YesNoEnum.yes)
        .filter(e -> Objects.equals(e.getEnabled(), YesNoEnum.yes))
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
