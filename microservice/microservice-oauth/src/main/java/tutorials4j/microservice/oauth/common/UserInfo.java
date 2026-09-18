package tutorials4j.microservice.oauth.common;

import lombok.Builder;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Builder
public class UserInfo {
  private String nickname;
  private String avatar;
  private String age;
  private String sex;
  private String address;
}
