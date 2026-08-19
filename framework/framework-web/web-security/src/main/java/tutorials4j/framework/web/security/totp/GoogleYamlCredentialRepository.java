package tutorials4j.framework.web.security.totp;

import cn.hutool.core.text.CharSequenceUtil;
import com.warrenstrange.googleauth.ICredentialRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import tutorials4j.framework.web.core.exception.WebErrorCode;
import tutorials4j.framework.web.security.properties.TotpWebProperties.CredentialOptions;

/**
 * 基于 YAML 配置的 TOTP 凭据仓库，从配置的凭据列表中初始化用户名与秘钥的映射。
 *
 * <p>实现 {@link ICredentialRepository} 接口，为 Google Authenticator 提供秘钥的读取与保存能力； 同时实现 {@link
 * InitializingBean}，在 Bean 初始化完成后将配置中的凭据列表加载到内存 Map 中。
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class GoogleYamlCredentialRepository implements ICredentialRepository, InitializingBean {

  private final List<CredentialOptions> credentialList;
  private final Map<String, String> credentialMap = new HashMap<>();

  /**
   * 根据用户名获取其 TOTP 秘钥；若该用户不存在则抛出业务异常。
   *
   * @param userName 用户名
   * @return 该用户名对应的 TOTP 秘钥
   * @throws tutorials4j.framework.common.core.exception.ErrorCodeException 用户名不存在时抛出
   */
  @Override
  public String getSecretKey(String userName) {
    String securityKey = credentialMap.get(userName);
    if (StringUtils.isBlank(securityKey)) {
      throw WebErrorCode.WEB_TOTP_SECRET_NOT_EXIST
          .throwed()
          .param("username", CharSequenceUtil.maxLength(userName, 4));
    }
    return securityKey;
  }

  /**
   * 保存用户的凭据信息（当前实现仅保存用户名与秘钥的映射）。
   *
   * @param userName 用户名
   * @param secretKey TOTP 秘钥
   * @param validationCode 校验码
   * @param scratchCodes 备用验证码列表
   */
  @Override
  public void saveUserCredentials(
      String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
    credentialMap.put(userName, secretKey);
  }

  /**
   * Bean 初始化完成后，将配置中的凭据列表加载到内存映射。
   *
   * @throws Exception 初始化过程发生异常时抛出
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    initializeMap();
  }

  /** 将配置中的凭据列表同步到内存映射（线程安全）。 */
  private void initializeMap() {
    synchronized (this.credentialMap) {
      this.credentialMap.clear();
      for (CredentialOptions options : credentialList) {
        this.credentialMap.put(options.getUsername(), options.getSecurityKey());
      }
    }
  }
}
