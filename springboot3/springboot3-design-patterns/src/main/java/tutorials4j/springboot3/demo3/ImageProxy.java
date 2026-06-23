package tutorials4j.springboot3.demo3;

// 图片代理类：控制真实图片的懒加载
public class ImageProxy implements Image {
  // 持有真实图片对象的引用（初始为空，不加载资源）
  private volatile RealImage realImage;
  // 图片资源路径
  private String imagePath;

  // 代理构造方法：仅保存路径，不加载任何资源（轻量初始化）
  public ImageProxy(String imagePath) {
    this.imagePath = imagePath;
    System.out.println("代理对象初始化完成，暂不加载图片资源");
  }

  /** 核心懒加载逻辑： 1、第一次调用展示方法时，才创建真实图片对象，加载资源 2、后续再次调用，直接复用已加载的资源，避免重复加载 */
  @Override
  public void display() {
    // 第一次无锁判断：避免每次都加锁，提升性能
    if (realImage == null) {
      // 加锁：保证同一时间只有一个线程创建对象
      synchronized (ImageProxy.class) {
        // 第二次锁内判断：防止多线程穿透
        if (realImage == null) {
          realImage = new RealImage(imagePath);
        }
      }
    }
    realImage.display();
  }

  // 释放图片资源，防止内存泄漏
  public void releaseImage() {
    if (realImage != null) {
      realImage = null;
      System.out.println("图片资源已释放，内存回收完成");
    }
  }
}
