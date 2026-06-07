package tutorials4j.framework.web.security.google;

import com.warrenstrange.googleauth.ICredentialRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import tutorials4j.framework.web.core.exception.WebFrameworkException;
import tutorials4j.framework.web.security.properties.GoogleWebProperties.CredentialOptions;

/**
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class GoogleYamlCredentialRepository implements ICredentialRepository, InitializingBean {

  private final List<CredentialOptions> credentialList;
  private final Map<String, String> credentialMap = new HashMap<>();

  @Override
  public String getSecretKey(String userName) {
    String securityKey = credentialMap.get(userName);
    if (StringUtils.isBlank(securityKey)) {
      throw new WebFrameworkException("凭据不存在");
    }
    return securityKey;
  }

  @Override
  public void saveUserCredentials(
      String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
    credentialMap.put(userName, secretKey);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    initializeMap();
  }

  private void initializeMap() {
    synchronized (this.credentialMap) {
      this.credentialMap.clear();
      for (CredentialOptions options : credentialList) {
        this.credentialMap.put(options.getUsername(), options.getSecurityKey());
      }
    }
  }
}
