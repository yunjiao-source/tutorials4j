package tutorials4j.toolkit.data.hibernate.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public abstract class BaseQuery<T> {

  protected final List<Specification<T>> specList = new ArrayList<>();

  protected abstract void addSpec();

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
