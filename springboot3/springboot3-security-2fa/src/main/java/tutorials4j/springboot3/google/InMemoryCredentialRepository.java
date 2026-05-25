package tutorials4j.springboot3.google;

import com.warrenstrange.googleauth.ICredentialRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 没有使用到，因为将密钥存储到了用户表中
 *
 * @author Yun Jiao
 */
@Service
public class InMemoryCredentialRepository implements ICredentialRepository {

  private final Map<String, String> credentials = new HashMap<>();

  @Override
  public String getSecretKey(String userName) {
    return credentials.get(userName);
  }

  @Override
  public void saveUserCredentials(
      String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
    credentials.put(userName, secretKey);
  }
}
