package tutorials4j.springboot3.web.resourceload;

import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

/**
 * 方式二：ResourceLoader（运行期动态加载）
 *
 * <p>适用场景：
 *
 * <p>资源路径 在运行期才确定 需要根据 外部环境/用户请求 动态读取文件 避免为每个资源声明一个字段
 *
 * @author yangyunjiao
 */
@Service
@RequiredArgsConstructor
public class ResourceLoaderDemoService {
  private final ResourceLoader resourceLoader;

  @Value("${config.path:resourceload}")
  private String configPath;

  public void print() {
    String location = "classpath:" + Paths.get(configPath, "config.properties").toString();
    Resource template = resourceLoader.getResource(location);
    Utils.print(template);
  }
}
