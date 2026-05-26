package tutorials4j.framework.web.google.auth;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import tutorials4j.framework.web.core.exception.WebFrameworkException;
import tutorials4j.framework.web.core.properties.GoogleAuthWebProperties.CredentialOptions;

/**
 * 基于 YAML 配置文件的 {@link XICredentialRepository} 实现。
 *
 * <p>从 {@link tutorials4j.framework.web.core.properties.GoogleAuthWebProperties#getCredentials()}
 * 中加载用户凭证（用户名、密码、秘钥）。
 *
 * <p>注意：该实现为内存存储，不支持动态添加或持久化秘钥更新（{@link #saveUserCredentials} 仅更新内存 Map， 不会回写至配置文件）。
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class YamlCredentialRepository implements XICredentialRepository {

  private final Map<String, CredentialOptions> credentialMap;

  @Override
  public String getSecretKey(String userName) {
    CredentialOptions options = getUser(userName);
    return options.getSecurityKey();
  }

  @Override
  public void saveUserCredentials(
      String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {

    CredentialOptions options = getUser(userName);
    options.setSecurityKey(secretKey);
  }

  @Override
  public boolean verifyPassword(String userName, String password) {
    CredentialOptions options = getUser(userName);
    return Objects.equals(options.getPassword(), password);
  }

  /**
   * 根据用户名获取凭证配置，如果用户不存在则抛出异常。
   *
   * @param userName 用户名
   * @return 对应的 CredentialOptions
   * @throws WebFrameworkException 如果用户不存在
   */
  public CredentialOptions getUser(String userName) {
    CredentialOptions options = credentialMap.get(userName);
    if (options == null) {
      throw new WebFrameworkException("用户不存在");
    }
    return options;
  }
}
