package tutorials4j.framework.feature.signin.jpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

/**
 * 签到结果查询条件。
 *
 * <p>支持按账号、来源及签到日期范围组合查询，并可构建对应的 JPA {@link Specification}。
 *
 * @author Yun Jiao
 */
@Data
public class SignInResultQuery {
  /** 签到账号 */
  private String account;

  /** 起始签到日期 */
  private LocalDate startSignDate;

  /** 截止签到日期 */
  private LocalDate endSignDate;

  /** 签到来源 */
  private String source;

  /**
   * 构建组合查询条件对应的 JPA Specification。
   *
   * @return 组合后的 Specification，无任何条件时返回恒真条件
   */
  public Specification<SignInResultEntity> buildSpecification() {
    List<Specification<SignInResultEntity>> specList = new ArrayList<>();
    specList.add(SignInResultSpecification.accountEqual(account));
    specList.add(SignInResultSpecification.sourceEqual(source));
    specList.add(SignInResultSpecification.signDateGte(startSignDate));
    specList.add(SignInResultSpecification.signDateLte(endSignDate));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
