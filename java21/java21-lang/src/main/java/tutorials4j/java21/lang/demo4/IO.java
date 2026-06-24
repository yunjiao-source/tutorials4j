package tutorials4j.java21.lang.demo4;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * I/O流性能对比：NIO、BIO、AIO该如何选择
 *
 * @author Yun Jiao
 */
public class IO {

  // BIO文件读取（同步阻塞）
  public static void readFileByBIO(String filePath) throws IOException {
    FileInputStream fis = new FileInputStream(filePath);
    byte[] buffer = new byte[1024];
    int len;
    // read()方法会阻塞，直到读取到数据或文件结束
    while ((len = fis.read(buffer)) != -1) {
      System.out.println(new String(buffer, 0, len));
    }
    fis.close(); // 手动关闭流（容易遗漏，导致资源泄漏）
  }

  // NIO文件读取（非阻塞）
  public static void readFileByNIO(String filePath) throws IOException {
    // 1. 获取通道
    FileChannel channel = FileChannel.open(Paths.get(filePath), StandardOpenOption.READ);
    // 2. 创建缓冲区（容量1024）
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    // 3. 读取数据到缓冲区（非阻塞，无数据时返回0，不阻塞线程）
    int len;
    while ((len = channel.read(buffer)) != -1) {
      buffer.flip(); // 切换为读模式
      System.out.println(new String(buffer.array(), 0, len));
      buffer.clear(); // 清空缓冲区，准备下一次读取
    }
    channel.close();
  }

  // AIO文件读取（异步非阻塞，回调通知）
  public static void readFileByAIO(String filePath) throws IOException {
    // 1. 获取异步通道
    AsynchronousFileChannel channel =
        AsynchronousFileChannel.open(Paths.get(filePath), StandardOpenOption.READ);
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    // 2. 发起异步读取，指定回调函数（I/O完成后触发）
    channel.read(
        buffer,
        0,
        null,
        new CompletionHandler<Integer, Void>() {
          // 读取成功时触发
          @Override
          public void completed(Integer result, Void attachment) {
            if (result != -1) {
              buffer.flip();
              System.out.println(new String(buffer.array(), 0, result));
              buffer.clear();
            }
            try {
              channel.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }

          // 读取失败时触发
          @Override
          public void failed(Throwable exc, Void attachment) {
            exc.printStackTrace();
          }
        });
    // 主线程不阻塞，可继续执行其他任务
    try {
      Thread.sleep(1000); // 等待回调执行完成
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
