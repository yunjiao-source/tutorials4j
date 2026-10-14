package tutorials4j.toolkit.core.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserModel {
  @EqualsAndHashCode.Include private String userId;

  private String username;

  private String password;

  private Set<String> authorities;

  private String name;

  private String nickname;

  private String phoneNumber;

  private String avatar;

  private String email;

  private String profile;

  private String website;

  private GenderEnum gender;

  private LocalDate birthdate;

  private Instant accountExpireAt;

  private Instant credentialsExpireAt;

  private ZoneId zoneInfo;

  private Locale localeInfo;

  private UserStatus status;

  private Instant createDate;

  private Instant lastModifiedDate;
}
