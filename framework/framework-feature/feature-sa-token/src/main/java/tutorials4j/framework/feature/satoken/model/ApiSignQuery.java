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
public class ApiSignQuery {
  private String appName;
  private Date createDateBegin;

  private Date createDateEnd;

  public Specification<ApiSignEntity> buildSpecification() {
    List<Specification<ApiSignEntity>> specList = new ArrayList<>();
    specList.add(ApiSignSpecification.appNameEqual(appName));
    specList.add(ApiSignSpecification.createDateBegin(createDateBegin));
    specList.add(ApiSignSpecification.createDateEnd(createDateEnd));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
