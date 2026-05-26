package tutorials4j.framework.common.spring.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class QrCodeUtilsTest {
  @Test
  void generateAsBase64SvgTest() throws IOException {
    String svg =
        QrCodeUtils.defaultGenerateAsBase64Svg("https://gitee.com/yunjiao-source/tutorials4j");
    Path outputPath = Paths.get("target", "output.html");
    Files.writeString(outputPath, "<html><body><img src=\"" + svg + "\" /></body></html>");
  }

  @Test
  void defaultGeneratePngTest() throws IOException {
    byte[] qrimageBytes =
        QrCodeUtils.defaultGeneratePng("https://gitee.com/yunjiao-source/tutorials4j");
    Path outputPath = Paths.get("target", "output.png");
    Files.write(outputPath, qrimageBytes);
  }
}
