package tutorials4j.framework.common.core.support;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * 字体缓存
 *
 * @author Yun Jiao
 */
@Slf4j
public final class FontCache {
  private static FontCache instance;

  // 字体缓存，存储字体名称和对应的Font对象
  private Map<String, Font> fontCache;

  // 系统可用字体列表
  private String[] availableFonts;

  /** 构造函数，初始化字体缓存和获取系统可用字体 */
  private FontCache() {
    fontCache = new ConcurrentHashMap<>();
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    availableFonts = ge.getAvailableFontFamilyNames();
  }

  /**
   * 获取实例。单例模式
   *
   * @return 实例
   */
  public static synchronized FontCache getInstance() {
    if (instance == null) {
      instance = new FontCache();
    }
    return instance;
  }

  /**
   * 注册单个字体流并缓存
   *
   * @param is 字体流文件
   * @throws FontFormatException 当字体格式不正确时抛出
   * @throws IOException 读取字体异常
   */
  public void registerFont(InputStream is) throws IOException, FontFormatException {
    Font font = Font.createFont(Font.TRUETYPE_FONT, is);
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    ge.registerFont(font);

    // 缓存字体
    String fontName = font.getFontName();
    fontCache.put(fontName, font);
    if (log.isDebugEnabled()) {
      log.debug("成功注册字体, fontName={}", fontName);
    }
  }

  /**
   * 获取字体
   *
   * @param fontName 字体名称
   * @param style 字体样式 (Font.PLAIN, Font.BOLD, Font.ITALIC)
   * @param size 字体大小
   * @return 请求的字体
   */
  public Font getFont(String fontName, int style, int size) {
    // 首先检查缓存
    if (fontCache.containsKey(fontName)) {
      return fontCache.get(fontName).deriveFont(style, size);
    }

    // 然后检查系统字体
    for (String availableFont : availableFonts) {
      if (availableFont.equalsIgnoreCase(fontName)) {
        // 创建并缓存系统字体
        Font font = new Font(fontName, style, size);
        fontCache.put(fontName, font);
        return font.deriveFont(style, size);
      }
    }

    // 字体不存在，使用默认字体
    Font defaultFont = new Font("SansSerif", style, size);
    fontCache.put(fontName, defaultFont);
    log.warn("系统中不存在字体：{}, 将使用默认字体：{}", fontName, defaultFont.getFamily());
    return defaultFont;
  }

  /**
   * 获取字体 (默认样式和大小)
   *
   * @param fontName 字体名称
   * @return 请求的字体
   */
  public Font getFont(String fontName) {
    return getFont(fontName, Font.PLAIN, 12);
  }

  /**
   * 检查字体是否可用
   *
   * @param fontName 字体名称
   * @return 如果字体可用返回true，否则返回false
   */
  public boolean isFontAvailable(String fontName) {
    // 检查缓存
    if (fontCache.containsKey(fontName)) {
      return true;
    }

    // 检查系统字体
    for (String availableFont : availableFonts) {
      if (availableFont.equalsIgnoreCase(fontName)) {
        return true;
      }
    }

    return false;
  }

  /**
   * 获取所有可用字体名称
   *
   * @return 所有可用字体名称的数组
   */
  public String[] getAllAvailableFonts() {
    return availableFonts;
  }

  /**
   * 获取所有已缓存字体名称
   *
   * @return 所有已缓存字体名称的数组
   */
  public String[] getAllCachedFonts() {
    return fontCache.keySet().toArray(new String[0]);
  }

  /** 清空字体缓存 */
  public void clearCache() {
    fontCache.clear();
  }
}
