package tutorials4j.springboot3.web.resourceload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * 工具
 *
 * @author yangyunjiao
 */
@Slf4j
public final class Utils {

  public static void print(Resource resource) {
    Assert.notNull(resource, "参数不能为null");
    if (!resource.exists()) {
      log.error("配置文件不存在");
      return;
    }
    try {
      String fileContent = resource.getContentAsString(StandardCharsets.UTF_8);
      log.info("配置文件内容：{}", fileContent);
    } catch (IOException e) {
      log.error("读取件失败", e);
    }

    // 使用 Properties 加载配置文件并读取特定属性
    try (InputStream input = resource.getInputStream()) {
      Properties props = new Properties();
      props.load(input);
      log.info("配置文件内容：{}", props);
    } catch (IOException e) {
      log.error("读取件失败", e);
    }
  }
}
