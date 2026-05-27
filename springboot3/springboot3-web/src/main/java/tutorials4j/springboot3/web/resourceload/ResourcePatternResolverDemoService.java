package tutorials4j.springboot3.web.resourceload;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

/**
 * 方式三：ResourcePatternResolver（批量加载） 适用场景：
 *
 * <p>一次加载 多个资源 资源符合某种命名规则 常用于 SQL 初始化脚本、批量模板、批量配置文件
 *
 * @author yangyunjiao
 */
@Service
@RequiredArgsConstructor
public class ResourcePatternResolverDemoService {
  private final ResourcePatternResolver resolver;

  public void print() throws IOException {
    Resource[] migrations = resolver.getResources("classpath:resourceload/*.properties");
    for (Resource resource : migrations) {
      Utils.print(resource);
    }
  }
}
