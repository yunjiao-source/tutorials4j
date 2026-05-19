package tutorials4j.framework.common.core.util;

import java.awt.image.BufferedImage;

/**
 * 高斯模糊算法实现
 *
 * @author Yun Jiao
 */
public final class GaussianBlur {
  /**
   * 轻度处理
   *
   * @param image 原始图像
   * @return 模糊后的图像
   */
  public static BufferedImage gaussianBlurLight(BufferedImage image) {
    return execute(image, 5);
  }

  /**
   * 中度处理
   *
   * @param image 原始图像
   * @return 模糊后的图像
   */
  public static BufferedImage gaussianBlurMedium(BufferedImage image) {
    return execute(image, 8);
  }

  /**
   * 重度处理
   *
   * @param image 原始图像
   * @return 模糊后的图像
   */
  public static BufferedImage gaussianBlurHeavy(BufferedImage image) {
    return execute(image, 11);
  }

  /**
   * 高斯模糊算法实现
   *
   * @param image 原始图像
   * @param radius 模糊半径
   * @return 模糊后的图像
   */
  public static BufferedImage execute(BufferedImage image, int radius) {
    int width = image.getWidth();
    int height = image.getHeight();

    // 创建结果图像
    BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    // 计算高斯核
    double[] kernel = createGaussianKernel(radius);
    int kernelSize = kernel.length;
    int kernelRadius = kernelSize / 2;

    // 临时存储中间结果
    int[] pixels = new int[width * height];
    int[] blurredPixels = new int[width * height];

    // 获取像素数据
    image.getRGB(0, 0, width, height, pixels, 0, width);

    // 水平模糊
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        double r = 0;
        double g = 0;
        double b = 0;
        double a = 0;

        for (int i = -kernelRadius; i <= kernelRadius; i++) {
          int pixelX = Math.min(Math.max(x + i, 0), width - 1);
          int pixelIndex = y * width + pixelX;
          int pixel = pixels[pixelIndex];

          double weight = kernel[i + kernelRadius];

          a += weight * ((pixel >> 24) & 0xFF);
          r += weight * ((pixel >> 16) & 0xFF);
          g += weight * ((pixel >> 8) & 0xFF);
          b += weight * (pixel & 0xFF);
        }

        int argb =
            ((int) a & 0xFF) << 24
                | ((int) r & 0xFF) << 16
                | ((int) g & 0xFF) << 8
                | ((int) b & 0xFF);

        blurredPixels[y * width + x] = argb;
      }
    }

    // 垂直模糊
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        double r = 0;
        double g = 0;
        double b = 0;
        double a = 0;

        for (int i = -kernelRadius; i <= kernelRadius; i++) {
          int pixelY = Math.min(Math.max(y + i, 0), height - 1);
          int pixelIndex = pixelY * width + x;
          int pixel = blurredPixels[pixelIndex];

          double weight = kernel[i + kernelRadius];

          a += weight * ((pixel >> 24) & 0xFF);
          r += weight * ((pixel >> 16) & 0xFF);
          g += weight * ((pixel >> 8) & 0xFF);
          b += weight * (pixel & 0xFF);
        }

        int argb =
            ((int) a & 0xFF) << 24
                | ((int) r & 0xFF) << 16
                | ((int) g & 0xFF) << 8
                | ((int) b & 0xFF);

        result.setRGB(x, y, argb);
      }
    }

    return result;
  }

  /**
   * 创建高斯核
   *
   * @param radius 模糊半径
   * @return 高斯核数组
   */
  public static double[] createGaussianKernel(int radius) {
    int size = radius * 2 + 1;
    double[] kernel = new double[size];
    double sigma = radius / 3.0;
    double sigma22 = 2 * sigma * sigma;
    double sqrtPiSigma22 = Math.sqrt(Math.PI * sigma22);
    double total = 0;

    // 计算高斯函数值
    for (int i = -radius; i <= radius; i++) {
      double distance = i * i;
      kernel[i + radius] = Math.exp(-distance / sigma22) / sqrtPiSigma22;
      total += kernel[i + radius];
    }

    // 归一化
    for (int i = 0; i < size; i++) {
      kernel[i] /= total;
    }

    return kernel;
  }
}
