package tutorials4j.framework.feature.satoken.model;

import java.util.ArrayList;
import java.util.Date;
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
public class ApiKeyQuery {
  private String namespace;
  private String title;

  private String loginId;

  private Boolean isValid;

  private Date createTimeBegin;

  private Date createTimeEnd;

  public Specification<ApiKeyEntity> buildSpecification() {
    List<Specification<ApiKeyEntity>> specList = new ArrayList<>();
    specList.add(ApiKeySpecification.titleLike(title));
    specList.add(ApiKeySpecification.namespaceEqual(namespace));
    specList.add(ApiKeySpecification.loginIdEqual(loginId));
    specList.add(ApiKeySpecification.isValidEqual(isValid));
    specList.add(ApiKeySpecification.createTimeBegin(createTimeBegin));
    specList.add(ApiKeySpecification.createTimeEnd(createTimeEnd));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
