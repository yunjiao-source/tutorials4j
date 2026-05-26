package tutorials4j.framework.web.google.auth;

import com.warrenstrange.googleauth.ICredentialRepository;

/**
 * 扩展的凭证仓库接口，继承自 {@link ICredentialRepository}。
 *
 * <p>增加了密码校验能力，允许在 TOTP 验证之外进行额外的密码验证（例如两步验证中的第一步）。
 *
 * @author Yun Jiao
 * @see ICredentialRepository
 */
public interface XICredentialRepository extends ICredentialRepository {

  /**
   * 校验给定用户的密码是否正确。
   *
   * @param userName 用户名
   * @param password 明文密码（实际比对时应使用编码后存储的值）
   * @return 密码匹配返回 {@code true}，否则返回 {@code false}
   */
  boolean verifyPassword(String userName, String password);
}
