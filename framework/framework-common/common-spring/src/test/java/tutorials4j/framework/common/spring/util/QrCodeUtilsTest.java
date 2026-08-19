package tutorials4j.framework.common.spring.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

/**
 * {@link QrCodeUtils} 单元测试。
 *
 * <p>验证默认的二维码生成工具方法，将生成结果输出到 target 目录下的文件中。
 *
 * @author Yun Jiao
 */
public class QrCodeUtilsTest {
  /**
   * 验证默认方式生成 Base64 格式的 SVG 二维码，并写入 {@code target/output.html}。
   *
   * @throws IOException 文件写入失败时抛出
   */
  @Test
  void generateAsBase64SvgTest() throws IOException {
    String svg =
        QrCodeUtils.defaultGenerateAsBase64Svg("https://gitee.com/yunjiao-source/tutorials4j");
    Path outputPath = Paths.get("target", "output.html");
    Files.writeString(outputPath, "<html><body><img src=\"" + svg + "\" /></body></html>");
  }

  /**
   * 验证默认方式生成 PNG 格式二维码，并写入 {@code target/output.png}。
   *
   * @throws IOException 文件写入失败时抛出
   */
  @Test
  void defaultGeneratePngTest() throws IOException {
    byte[] qrimageBytes =
        QrCodeUtils.defaultGeneratePng("https://gitee.com/yunjiao-source/tutorials4j");
    Path outputPath = Paths.get("target", "output.png");
    Files.write(outputPath, qrimageBytes);
  }
}
