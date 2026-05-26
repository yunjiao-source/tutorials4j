package tutorials4j.springboot3.web.captcha;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 验证码工具
 *
 * @author Yun Jiao
 */
public final class CaptchaUtil {

  public static Pair<String, BufferedImage> createVerificationImage() {
    String code = RandomStringUtils.secure().nextAlphabetic(5);
    BufferedImage image = new BufferedImage(100, 40, BufferedImage.TYPE_INT_ARGB);

    Graphics graphics = image.getGraphics();
    graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    graphics.setColor(Color.BLACK);

    for (int i = 0; i < code.length(); i++) {
      graphics.drawString(code.charAt(i) + "", 10 + i * 16, 32);
    }

    image = applyArtisticEffects(image);

    return Pair.of(code, image);
  }

  // 旋转给定的图像
  private static BufferedImage rotateImage(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();

    BufferedImage rotatedImage = new BufferedImage(width, height, image.getType());
    Graphics2D graphics2D = rotatedImage.createGraphics();

    double theta = Math.toRadians(new Random().nextInt(40) - 20); // 在-20到20度之间随机旋转
    graphics2D.rotate(theta, (double) width / 2, (double) height / 2);
    graphics2D.drawImage(image, 0, 0, null);
    graphics2D.dispose();

    return rotatedImage;
  }

  // 对给定的字符串应用底噪音和干扰线
  private static BufferedImage applyArtisticEffects(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();

    Random random = new Random();

    // 底部噪声
    for (int i = 0; i < 30; i++) {
      int x = random.nextInt(width);
      int y = random.nextInt(height);
      int rgb = getRandomRgb();
      image.setRGB(x, y, rgb);
    }

    // 干扰线
    Graphics2D graphics2D = image.createGraphics();
    for (int i = 0; i < 5; i++) {
      int x = random.nextInt(width);
      int y = random.nextInt(height);
      int xl = random.nextInt(width);
      int yl = random.nextInt(height);
      graphics2D.setColor(new Color(getRandomRgb()));
      graphics2D.drawLine(x, y, x + xl, y + yl);
    }

    graphics2D.dispose();

    return rotateImage(image);
  }

  // 生成随机的RGB颜色
  private static int getRandomRgb() {
    Random random = new Random();
    int red = random.nextInt(256);
    int green = random.nextInt(256);
    int blue = random.nextInt(256);

    return (red << 16) | (green << 8) | blue;
  }
}
