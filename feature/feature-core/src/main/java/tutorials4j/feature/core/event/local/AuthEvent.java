package tutorials4j.feature.core.event.local;

import lombok.Data;
import tutorials4j.feature.core.model.AuthStatus;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Data
public class AuthEvent {
  private String userId;

  private String username;

  private String name;

  private String phoneNumber;

  private String email;

  private String ipAddress;

  private String region;

  private String platform;

  private String browser;

  private AuthStatus status;
}
