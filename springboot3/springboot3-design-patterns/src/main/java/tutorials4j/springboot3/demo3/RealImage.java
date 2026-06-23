package tutorials4j.springboot3.demo3;

// 真实图片类：负责加载真实图片资源（耗时、耗内存）
public class RealImage implements Image {
  // 图片资源路径（本地路径/网络URL）
  private String imagePath;

  // 构造方法：初始化路径，同时加载图片（耗时操作）
  public RealImage(String imagePath) {
    this.imagePath = imagePath;
    // 模拟图片加载耗时：高清大图/网络图片加载延迟
    loadImage();
  }

  /** 核心耗时方法：加载图片资源 对应开发中的IO读取、网络请求、图片解码 */
  private void loadImage() {
    System.out.println("开始加载图片资源，路径：" + imagePath);
    try {
      // 模拟2秒耗时，真实场景中为IO/网络耗时
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("图片资源加载完成！");
  }

  // 重写展示方法：对外展示图片
  @Override
  public void display() {
    System.out.println("展示图片：" + imagePath);
  }
}
