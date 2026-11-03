package tutorials4j.toolkit.data.hibernate.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;
import tutorials4j.toolkit.core.constant.DefaultConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public abstract class BaseQuery<T> {
  @Getter private int maxQueryLimit = DefaultConsts.MAX_JPA_FIND_COUNT_BY_QUERY;

  protected final List<Specification<T>> specList = new ArrayList<>();

  protected abstract void addSpec();

  public BaseQuery<T> queryLimit(int limit) {
    Assert.isTrue(limit > 0, "limit must be greater than zero");
    maxQueryLimit = limit;
    return this;
  }

  public BaseQuery<T> noQueryLimit() {
    maxQueryLimit = -1;
    return this;
  }

  public BaseQuery<T> addSpec(Specification<T> spec) {
    Assert.notNull(spec, "spec must not be null");
    specList.add(spec);
    return this;
  }

  public BaseQuery<T> addSpec(Boolean condition, Specification<T> spec) {
    if (condition == null || !condition) {
      return this;
    }

    Assert.notNull(spec, "spec must not be null");
    specList.add(spec);
    return this;
  }

  public Specification<T> buildSpecification() {
    addSpec();

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
