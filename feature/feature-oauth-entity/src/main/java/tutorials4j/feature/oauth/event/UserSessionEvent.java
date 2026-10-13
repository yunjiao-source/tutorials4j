package tutorials4j.feature.oauth.event;

import lombok.Data;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Data
public class UserSessionEvent {
  private String username;

  private String name;

  private String phoneNumber;

  private String email;

  private String IpAddress;

  private String region;

  private String platForm;

  private String browser;

  private SessionType type;

  public static enum SessionType {
    logout,
    loginSuccess,
    loginFailure
  }
}
