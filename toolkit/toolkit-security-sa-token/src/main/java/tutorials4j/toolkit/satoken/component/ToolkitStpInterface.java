package tutorials4j.toolkit.satoken.component;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpInterface;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import tutorials4j.toolkit.core.constant.ToolkitConsts;
import tutorials4j.toolkit.satoken.autoconfigure.SaTokenSecurityToolkitProperties.PermissionOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public abstract class ToolkitStpInterface implements StpInterface {
  protected final PermissionOptions permissionOptions;

  protected abstract List<String> getAllPermissions();

  protected abstract List<String> getPermissionsByRole(String role);

  protected abstract List<String> getRoleByUsername(String username);

  @Override
  @SuppressWarnings("unchecked")
  public List<String> getPermissionList(Object loginId, String loginType) {
    // 1. 声明权限码集合
    var set = new HashSet<String>();

    // 2. 遍历角色列表，查询拥有的权限码
    for (String roleId : getRoleList(loginId, loginType)) {
      var roleKey = permissionOptions.getRoleKeyPrefix() + roleId;
      var permissionList = (List<String>) SaManager.getSaTokenDao().getObject(roleKey);
      if (permissionList == null) {
        // 从数据库查询这个角色 id 所拥有的权限列表
        if (ToolkitConsts.SUPER_ROLE_NAME.equals(roleId)) {
          // 超级角色
          permissionList = getAllPermissions();
        } else {
          permissionList = getPermissionsByRole(roleId);
        }

        // 查好后，set 到缓存中
        SaManager.getSaTokenDao()
            .setObject(
                roleKey,
                new ArrayList<>(permissionList),
                permissionOptions.getRoleKeyExpired().toSeconds());
      }
      set.addAll(permissionList);
    }

    // 3. 返回权限码集合
    return new ArrayList<>(set);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<String> getRoleList(Object loginId, String loginType) {
    var userKey = permissionOptions.getUserKeyPrefix() + loginId;
    var roleList = (List<String>) SaManager.getSaTokenDao().getObject(userKey);
    if (roleList == null) {
      // 从数据库查询这个账号id拥有的角色列表，
      roleList = getRoleByUsername(loginId.toString());
      // 查好后，set 到缓存中
      SaManager.getSaTokenDao()
          .setObject(
              userKey,
              new ArrayList<>(roleList),
              permissionOptions.getUserKeyDuration().toSeconds());
    }
    return roleList;
  }
}
