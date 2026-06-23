package tutorials4j.springboot3.demo3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DemoRunner implements CommandLineRunner {

  @Override
  public void run(String... args) throws Exception {
    System.out.println("=====项目启动，初始化资源=====");
    // 启动时仅创建代理对象，无任何图片资源加载，启动极速
    Image image = new ImageProxy("D:/images/高清海报图.jpg");
    System.out.println("=====业务空闲中，未使用图片资源=====");
    // 模拟业务逻辑执行（1秒空闲，用户未触发图片展示）
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("=====用户触发图片展示，开始懒加载=====");
    // 首次调用：触发真实图片加载、展示
    image.display();
    System.out.println("=====二次展示图片，复用资源=====");
    // 二次调用：无需重新加载，直接展示（性能极高）
    image.display();
  }
}
