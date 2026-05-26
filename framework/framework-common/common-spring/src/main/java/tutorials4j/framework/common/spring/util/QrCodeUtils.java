package tutorials4j.framework.common.spring.util;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import java.io.File;
import java.io.FileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

/**
 * 二维码生成工具类，基于 Hutool 的 {@link QrCodeUtil} 扩展。
 *
 * <p>该类提供了预置默认配置的便捷方法，默认生成尺寸为 300x300 的二维码， 并自动加载 classpath 下的 <code>t4j.png</code> 作为 Logo（若存在）。
 * 支持生成 PNG、SVG、TXT 及 ASCII Art 格式的二维码，并能以 Base64 字符串或字节数组形式返回。
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * // 生成带 Logo 的 PNG 二维码 Base64 字符串
 * String base64Png = QrCodeUtils.defaultGenerateAsBase64Png("https://example.com");
 *
 * // 生成 ASCII Art 二维码
 * String asciiArt = QrCodeUtils.defaultGenerateAsAsciiArt("Hello World");
 * }</pre>
 *
 * @author Yun Jiao
 * @see QrCodeUtil
 * @see QrConfig
 */
@Slf4j
public class QrCodeUtils extends QrCodeUtil {
  private static final QrConfig DEFAULT_CONFIG;
  private static File logo;

  static {
    DEFAULT_CONFIG = new QrConfig(300, 300);

    try {
      logo = ResourceUtils.getFile("classpath:t4j-120x120.png");
      DEFAULT_CONFIG.setImg(logo);
    } catch (FileNotFoundException e) {
      log.error("加载图片异常", e);
    }
  }

  /**
   * 创建一个新的默认配置对象。
   *
   * <p>该配置与类内部静态默认配置一致：尺寸 300x300，并设置相同的 Logo（若 Logo 文件成功加载）。 适用于需要独立配置但希望复用默认参数的场景。
   *
   * @return 新的 {@link QrConfig} 实例，携带默认尺寸和 Logo（如存在）
   */
  public static QrConfig newDefaultConfig() {
    QrConfig config = new QrConfig(300, 300);
    if (logo != null) {
      config.setImg(logo);
    }
    return config;
  }

  /**
   * 使用默认配置生成 SVG 格式二维码，并返回 Base64 编码的字符串。
   *
   * <p>等同于调用 {@link #defaultGenerateAsBase64(String, String)} 并指定 targetType 为 {@link
   * QrCodeUtil#QR_TYPE_SVG}。
   *
   * @param content 二维码携带的内容（文本或 URL）
   * @return SVG 格式二维码的 Base64 字符串，可直接用于 img 标签的 src 属性（如 "data:image/svg+xml;base64,..."）
   */
  public static String defaultGenerateAsBase64Svg(String content) {
    return defaultGenerateAsBase64(content, QR_TYPE_SVG);
  }

  /**
   * 使用默认配置生成文本格式二维码，并返回 Base64 编码的字符串。
   *
   * <p>等同于调用 {@link #defaultGenerateAsBase64(String, String)} 并指定 targetType 为 {@link
   * QrCodeUtil#QR_TYPE_TXT}。 文本格式适合调试或纯文本环境。
   *
   * @param content 二维码携带的内容
   * @return 文本格式二维码的 Base64 字符串
   */
  public static String defaultGenerateAsBase64Txt(String content) {
    return defaultGenerateAsBase64(content, QR_TYPE_TXT);
  }

  /**
   * 使用默认配置生成 PNG 格式二维码，并返回 Base64 编码的字符串。
   *
   * <p>等同于调用 {@link #defaultGenerateAsBase64(String, String)} 并指定 targetType 为 "png"。 返回的字符串可直接用于
   * HTML 图片元素，例：<code>data:image/png;base64,{base64}</code>。
   *
   * @param content 二维码携带的内容
   * @return PNG 格式二维码的 Base64 字符串
   */
  public static String defaultGenerateAsBase64Png(String content) {
    return defaultGenerateAsBase64(content, "png");
  }

  /**
   * 使用默认配置生成指定格式的二维码，并返回 Base64 编码的字符串。
   *
   * <p>格式参数 {@code targetType} 支持 Hutool 定义的常量（如 {@link QrCodeUtil#QR_TYPE_SVG}、{@link
   * QrCodeUtil#QR_TYPE_TXT}） 或常见图片格式（如 "png"、"jpg"）。
   *
   * @param content 二维码携带的内容
   * @param targetType 输出格式类型（如 "png", "svg", "txt"）
   * @return 对应格式二维码的 Base64 字符串
   * @see QrCodeUtil#generateAsBase64(String, QrConfig, String)
   */
  public static String defaultGenerateAsBase64(String content, String targetType) {
    return generateAsBase64(content, DEFAULT_CONFIG, targetType);
  }

  /**
   * 使用默认配置生成 PNG 格式二维码，并返回字节数组。
   *
   * <p>字节数组可直接写入文件或响应流，适合需要二进制数据的场景。
   *
   * @param content 二维码携带的内容
   * @return PNG 图片的字节数组
   * @see QrCodeUtil#generatePng(String, QrConfig)
   */
  public static byte[] defaultGeneratePng(String content) {
    return generatePng(content, DEFAULT_CONFIG);
  }

  /**
   * 使用默认配置生成 ASCII Art 字符串形式的二维码。
   *
   * <p>返回的字符串由字符拼凑成二维码图案，适用于控制台输出或纯文本环境。
   *
   * @param content 二维码携带的内容
   * @return ASCII Art 格式的二维码字符串
   * @see QrCodeUtil#generateAsAsciiArt(String, QrConfig)
   */
  public static String defaultGenerateAsAsciiArt(String content) {
    return generateAsAsciiArt(content, DEFAULT_CONFIG);
  }
}
