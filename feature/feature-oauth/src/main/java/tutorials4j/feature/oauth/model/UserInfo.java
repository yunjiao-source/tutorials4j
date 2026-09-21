package tutorials4j.feature.oauth.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import lombok.Data;
import tutorials4j.feature.oauth.entity.UserEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class UserInfo {
  private String sub;
  private String name;
  private String nickname;
  private String preferred_username;
  private String profile;
  private String picture;
  private String website;
  private String email;
  private String phoneNumber;
  private GenderEnum gender;
  private LocalDate birthdate;
  private ZoneId zoneInfo;
  private Locale locale;
  private Instant updated_at;

  public void fillByProfile(UserEntity entity) {
    this.name = entity.getName();
    this.nickname = entity.getNickname();
    this.preferred_username = entity.getUsername();
    this.profile = entity.getProfile();
    this.picture = entity.getAvatar();
    this.website = entity.getWebsite();
    this.gender = entity.getGender();
    this.birthdate = entity.getBirthdate();
    this.zoneInfo = entity.getZoneInfo();
    this.locale = entity.getLocaleInfo();
    this.updated_at = entity.getLastModifiedDate();
  }
}
