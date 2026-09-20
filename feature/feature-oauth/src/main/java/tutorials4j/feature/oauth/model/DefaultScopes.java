package tutorials4j.feature.oauth.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum DefaultScopes {
  openid("认证ID"),
  profile("个人资料信息, 包括：姓名，账户，邮箱等"),
  email("电子邮件地址"),
  address("地址信息"),
  phone("手机号码"),
  offline_access("离线访问");

  private final String description;
  private static Map<String, DefaultScopes> BY_NAME;

  DefaultScopes(String description) {
    this.description = description;
  }

  private static synchronized void initByName() {
    if (BY_NAME == null) {
      BY_NAME =
          Arrays.stream(values())
              .collect(Collectors.toMap(s -> s.name().toLowerCase(), s -> s, (a, b) -> a));
    }
  }

  public static List<String> getAllScopes() {
    return Arrays.stream(values()).map(DefaultScopes::name).collect(Collectors.toList());
  }

  public static Map<String, String> getScopeNames(Collection<String> scopes) {
    var scopeNames = new LinkedHashMap<String, String>();

    if (scopes == null || scopes.isEmpty()) {
      return scopeNames;
    }

    if (BY_NAME == null) {
      initByName();
    }

    for (String scope : scopes) {
      if (scope == null) {
        continue;
      }

      DefaultScopes matched = BY_NAME.get(scope.toLowerCase());
      if (matched != null) {
        scopeNames.put(matched.name(), matched.description);
      }
    }

    return scopeNames;
  }
}
