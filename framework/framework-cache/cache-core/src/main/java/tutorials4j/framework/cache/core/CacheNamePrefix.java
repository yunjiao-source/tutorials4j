package tutorials4j.framework.cache.core;

import java.util.Objects;
import org.springframework.util.Assert;
import tutorials4j.framework.common.core.SymbolConsts;
import tutorials4j.framework.common.core.TenantContextHolder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface CacheNamePrefix {
  String SEPARATOR = SymbolConsts.COLON;

  String DOUBLE_SEPARATOR = "::";

  String compute(String cacheName);

  static CacheNamePrefix tenant() {
    return name -> TenantContextHolder.get() + SEPARATOR + name;
  }

  static CacheNamePrefix simple() {
    return name -> name;
  }

  static CacheNamePrefix prefix(String prefix) {
    Assert.notNull(prefix, "prefix must not be null");
    return name -> prefix + name;
  }

  default CacheNamePrefix end() {
    return (name) -> this.compute(name) + DOUBLE_SEPARATOR;
  }

  default CacheNamePrefix suffix(String suffix) {
    return (name) -> prefix(suffix).andThen(this).compute(name);
  }

  default CacheNamePrefix compose(CacheNamePrefix before) {
    Objects.requireNonNull(before);
    return (name) -> compute(before.compute(name));
  }

  default CacheNamePrefix andThen(CacheNamePrefix after) {
    Objects.requireNonNull(after);
    return (name) -> after.compute(compute(name));
  }
}
