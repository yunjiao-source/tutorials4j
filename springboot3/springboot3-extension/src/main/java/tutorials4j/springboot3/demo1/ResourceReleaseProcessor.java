package tutorials4j.springboot3.demo1;

import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

// 全局资源统一释放
@Component
public class ResourceReleaseProcessor implements DestructionAwareBeanPostProcessor {
  @Override
  public void postProcessBeforeDestruction(Object bean, String beanName) {
    // 统一关闭所有可关闭资源客户端
    if (bean instanceof CloseableClient) {
      ((CloseableClient) bean).close();
      System.out.println("服务优雅下线：资源【" + beanName + "】关闭成功");
    }
    // 可扩展线程池、文件流、缓存等资源释放
  }

  @Override
  public boolean requiresDestruction(Object bean) {
    // 仅监听可关闭资源类，提升性能
    return bean instanceof CloseableClient;
  }
}
