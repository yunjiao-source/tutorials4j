package tutorials4j.toolkit.cache.component;

import java.util.List;
import java.util.Objects;
import lombok.Setter;
import org.springframework.cache.CacheManager;
import tutorials4j.toolkit.cache.CacheErrorCode;
import tutorials4j.toolkit.cache.CacheType;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Setter
public class CacheManagerFactory {
  public static final CacheManagerFactory instance = new CacheManagerFactory();
  private List<CacheManagerCreator<?>> creators;

  public CacheManager getCacheManager(CacheType type) {
    for (CacheManagerCreator<?> creator : creators) {
      if (Objects.equals(type, creator.getCacheType())) {
        return creator.getInstance();
      }
    }

    throw CacheErrorCode.CACHE_MANAGER_NOT_EXIST.throwed().param("cacheType", type);
  }
}
