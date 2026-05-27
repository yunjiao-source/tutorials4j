package tutorials4j.springboot3.web.resourceload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * 方式一：@Value + Resource（最简单、最常用）
 *
 * <p>适用场景： 资源路径 在编译期就能确定 文件数量不多 常用于读取配置、模板、固定文本 常见前缀包括：classpath:、 file:、http://、https://
 *
 * @author yangyunjiao
 */
@Service
public class ValueDemoService {
  //  注入 classpath 下的配置文件
  @Value("classpath:resourceload/config.properties")
  private Resource classpathResource;

  public void print() {
    // 打印 config.properties 文件的完整内容
    Utils.print(classpathResource);
  }
}
