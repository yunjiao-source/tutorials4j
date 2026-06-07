package tutorials4j.framework.feature.domain.signin;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class SignInResultQuery {
  private String account;
  private LocalDate startSignDate;
  private LocalDate endSignDate;
  private String source;

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
