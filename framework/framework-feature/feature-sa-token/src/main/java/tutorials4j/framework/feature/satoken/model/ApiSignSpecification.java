package tutorials4j.framework.feature.satoken.model;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ApiSignSpecification {

  public static Specification<ApiSignEntity> appNameEqual(String appName) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(appName)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("appName"), appName);
    };
  }

  public static Specification<ApiSignEntity> createDateBegin(Date createDateBegin) {
    return (root, query, cb) -> {
      if (createDateBegin == null) {
        return cb.conjunction();
      }
      return cb.greaterThanOrEqualTo(root.get("createDate"), createDateBegin);
    };
  }

  public static Specification<ApiSignEntity> createDateEnd(Date createDateEnd) {
    return (root, query, cb) -> {
      if (createDateEnd == null) {
        return cb.conjunction();
      }
      return cb.lessThanOrEqualTo(root.get("createDate"), createDateEnd);
    };
  }
}
