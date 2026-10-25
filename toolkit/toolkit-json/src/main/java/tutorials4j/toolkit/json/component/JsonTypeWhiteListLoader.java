package tutorials4j.toolkit.json.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import tutorials4j.toolkit.core.constant.DefaultConsts;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
public class JsonTypeWhiteListLoader implements SmartInitializingSingleton {
  private static final String FILE_NAME = "json-type-white-list.conf";
  private static final String LOCATION_PATTERN =
      "classpath*:/" + DefaultConsts.META_TUTORIALS4J_DIR + "/" + FILE_NAME;

  private final Set<String> classNames = new HashSet<>();

  public boolean contains(String className) {
    boolean exist = classNames.contains(className);
    if (exist) {
      return true;
    }

    for (String name : classNames) {
      boolean isSubType = className.startsWith(name);
      if (isSubType) {
        return true;
      }
    }

    return false;
  }

  public Set<String> load() throws IOException {
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource[] resources = resolver.getResources(LOCATION_PATTERN);

    Set<String> result = new HashSet<>();
    for (Resource resource : resources) {
      loadFromResource(resource, result);
    }
    if (log.isDebugEnabled()) {
      log.debug("加载Json类型白名单完成：{}", result);
    }
    return result;
  }

  private static void loadFromResource(Resource resource, Set<String> result) throws IOException {
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
      reader
          .lines()
          .map(String::trim)
          .filter(line -> !line.isEmpty() && !line.startsWith("#"))
          .forEach(result::add);
    }
  }

  @Override
  public void afterSingletonsInstantiated() {
    try {
      classNames.addAll(load());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
