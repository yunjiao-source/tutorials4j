package tutorials4j.feature.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import tutorials4j.feature.oauth.component.ZoneIdConverter;
import tutorials4j.feature.oauth.model.GenderEnum;
import tutorials4j.feature.oauth.model.UserStatus;
import tutorials4j.toolkit.data.hibernate.domain.BaseEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(
    name = "oauth_user",
    indexes = {@Index(name = "oauth_user_idx0", columnList = "status")},
    uniqueConstraints = {@UniqueConstraint(name = "oauth_user_id10", columnNames = "username")})
@NamedEntityGraphs({
  @NamedEntityGraph(
      name = EntityGraphConsts.USER_WITH_ROLE,
      attributeNodes = @NamedAttributeNode("roles")),
  @NamedEntityGraph(
      name = EntityGraphConsts.USER_WITH_ROLE_PERMISSION,
      attributeNodes = {@NamedAttributeNode(value = "roles", subgraph = "role.permissions")},
      subgraphs = {
        @NamedSubgraph(
            name = "role.permissions",
            attributeNodes = @NamedAttributeNode("permissions"))
      })
})
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

  @ManyToMany
  @Fetch(FetchMode.SUBSELECT)
  @JoinTable(
      name = "oauth_user_role",
      joinColumns = {@JoinColumn(name = "user_id")},
      inverseJoinColumns = {@JoinColumn(name = "role_id")},
      uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"})},
      indexes = {
        @Index(name = "oauth_user_role_idx1", columnList = "user_id"),
        @Index(name = "oauth_user_role_idx2", columnList = "role_id")
      })
  private Set<RoleEntity> roles = new HashSet<>();
}
