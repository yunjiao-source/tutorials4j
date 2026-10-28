package tutorials4j.feature.upms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.feature.upms.util.ZoneIdConverter;
import tutorials4j.toolkit.core.model.GenderEnum;
import tutorials4j.toolkit.core.model.UserStatus;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.exception.DataErrorCode;
import tutorials4j.toolkit.data.hibernate.domain.BaseEntity;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
@Entity
@Table(
    name = "upms_user",
    indexes = {@Index(name = "upms_user_idx1", columnList = "status")},
    uniqueConstraints = {@UniqueConstraint(name = "upms_user_uni1", columnNames = "username")})
public class UserEntity extends BaseEntity<String> {

  /** 用户名 */
  @Column(nullable = false)
  private String username;

  /** 密码 */
  @Column private String password;

  /** 姓名 */
  @Column private String name;

  /** 昵称 */
  @Column private String nickname;

  /** 手机号码 */
  @Column private String phoneNumber;

  /** 头像 */
  @Column private String avatar;

  /** 邮箱 */
  @Column private String email;

  /** 个人简介 */
  @Column private String profile;

  /** 个人网站 */
  @Column private String website;

  /** 性别 */
  @Column
  @Enumerated(EnumType.STRING)
  private GenderEnum gender;

  /** 生日 */
  @Column private LocalDate birthdate;

  /** 账户过期日期 */
  @Column private Instant accountExpireAt;

  /** 密码过期日期 */
  @Column private Instant credentialsExpireAt;

  /** 时区，例如 "Europe/Paris" 或 "America/New_York" */
  @Column
  @Convert(converter = ZoneIdConverter.class)
  private ZoneId zoneInfo;

  /** 区域设置，例如 "en-US" 或 "zh-CN" */
  @Column private Locale localeInfo;

  /** 状态 */
  @Column
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum reserved;

  @Column(name = "organize_id", insertable = false, updatable = false)
  private String organizeId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "organize_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "upms_user_fk1"))
  private OrganizeEntity organize;

  @ManyToMany
  @JoinTable(
      name = "upms_user_role",
      joinColumns = {
        @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "upms_user_role_fk1"))
      },
      inverseJoinColumns = {
        @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "upms_user_role_fk2"))
      },
      uniqueConstraints = {
        @UniqueConstraint(
            name = "upms_user_role_uni1",
            columnNames = {"user_id", "role_id"})
      },
      indexes = {
        @Index(name = "upms_user_role_idx1", columnList = "user_id"),
        @Index(name = "upms_user_role_idx2", columnList = "role_id")
      })
  private Set<RoleEntity> roles = new HashSet<>();

  @Override
  protected void prePersist() {
    super.prePersist();

    if (reserved == null) {
      reserved = YesNoEnum.no;
    }
  }

  @Override
  protected void preRemove() {
    super.preRemove();

    if (YesNoEnum.isTrue(reserved)) {
      throw DataErrorCode.RESERVED_DATA_CAN_NOT_REMOVE
          .throwed()
          .param("className", getClass().getName());
    }
  }
}
