package tutorials4j.feature.basic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@Entity
@Table(
    name = "basic_company",
    uniqueConstraints = {
      @UniqueConstraint(name = "basic_company_uni1", columnNames = "organize_code")
    })
public class CompanyEntity extends BasicEntity {
  @Column private String organizeCode;

  @Column private String name;

  @Column private String shortName;

  // 公司性质
  @Column private String companyType;

  // 所属行业
  @Column private String industry;

  // 成立时间
  @Column private Instant establishmentDate;

  // 公司电话
  @Column private String phone;

  // 公司传真
  @Column private String fax;

  // 公司主页
  @Column private String website;

  // 公司地址
  @Column private String address;

  // 公司法人
  @Column private String legalRepresentative;

  // 联系电话
  @Column private String contactPhone;

  // 联系手机
  @Column private String contactMobile;

  // 联系邮箱
  @Column private String contactEmail;

  // 开户银行
  @Column private String openingBank;

  // 银行账户
  @Column private String bankAccount;

  // 经营范围
  @Column private String businessScope;
}
