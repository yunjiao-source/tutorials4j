package tutorials4j.springboot3.integration.googleauth.google;

import java.io.ByteArrayOutputStream;
import net.glxn.qrgen.QRCode;

/**
 * 二维码工具类
 *
 * @author Yun Jiao
 */
public class QRCodeUtil {

  /**
   * 生成二维码图片
   *
   * @param barcodeURL 二维码 URL
   * @return 二维码图片字节数组
   */
  public static byte[] generateQRCode(String barcodeURL) {
    ByteArrayOutputStream stream = QRCode.from(barcodeURL).withSize(250, 250).stream();
    return stream.toByteArray();
  }
}
