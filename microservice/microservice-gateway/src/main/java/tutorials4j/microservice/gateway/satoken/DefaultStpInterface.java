package tutorials4j.microservice.gateway.satoken;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.entity.PermissionEntity;
import tutorials4j.feature.oauth.entity.RoleEntity;
import tutorials4j.feature.oauth.service.RoleService;
import tutorials4j.feature.oauth.service.UserService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DefaultStpInterface implements StpInterface {
  protected static final String ROLE_KEY_PREFIX = "satoken:role-find-permission:";
  protected static final String USER_KEY_PREFIX = "satoken:user-find-role:";
  private final RoleService roleService;
  private final UserService userService;

  @Override
  @SuppressWarnings("unchecked")
  public List<String> getPermissionList(Object loginId, String loginType) {
    // 1. 声明权限码集合
    var list = new ArrayList<String>();

    // 2. 遍历角色列表，查询拥有的权限码
    for (String roleId : getRoleList(loginId, loginType)) {
      var permissionList =
          (List<String>) SaManager.getSaTokenDao().getObject(ROLE_KEY_PREFIX + roleId);
      if (permissionList == null) {
        // 从数据库查询这个角色 id 所拥有的权限列表
        permissionList =
            roleService
                .getRoleRepository()
                .findByCode(roleId)
                .map(r -> r.getPermissions().stream().map(PermissionEntity::getCode).toList())
                .orElse(Collections.emptyList());

        // 查好后，set 到缓存中
        SaManager.getSaTokenDao()
            .setObject(ROLE_KEY_PREFIX + roleId, permissionList, 60 * 60 * 24 * 30);
      }
      list.addAll(permissionList);
    }

    // 3. 返回权限码集合
    return list;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<String> getRoleList(Object loginId, String loginType) {
    List<String> roleList =
        (List<String>) SaManager.getSaTokenDao().getObject(USER_KEY_PREFIX + loginId);
    if (roleList == null) {
      // 从数据库查询这个账号id拥有的角色列表，
      roleList =
          userService
              .getUserRepository()
              .findWithRolesByUsername(loginId.toString())
              .map(u -> u.getRoles().stream().map(RoleEntity::getCode).toList())
              .orElse(Collections.emptyList());
      // 查好后，set 到缓存中
      SaManager.getSaTokenDao().setObject(USER_KEY_PREFIX + loginId, roleList, 60 * 60 * 24 * 30);
    }
    return roleList;
  }
}
