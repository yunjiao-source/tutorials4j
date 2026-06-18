package tutorials4j.framework.common.spring.util;

import java.io.IOException;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.owasp.validator.html.*;
import org.springframework.util.ResourceUtils;

/**
 * XSS 攻击防御工具类。
 *
 * <p>基于 OWASP AntiSamy 实现，通过策略文件（antisamy-anythinggoes.xml）对用户输入的 HTML 或文本进行过滤， 移除恶意脚本及危险的 HTML
 * 标签/属性。同时处理特殊字符转义及乱码问题。
 *
 * <p>该类为单例模式，内部持有 {@link AntiSamy} 实例及清洗过程中产生的特殊字符占位符（&nbsp;、双引号等）。
 *
 * @author Yun Jiao
 * @see AntiSamy
 * @since 1.0
 */
@Slf4j
public class XssUtils {
  private static XssUtils INSTANCE;
  private final AntiSamy antiSamy;
  private final String nbsp;
  private final String quot;

  /**
   * 私有构造器，创建 AntiSamy 实例并预清理特殊字符。
   *
   * <p>尝试从 classpath 加载策略文件 {@code antisamy/antisamy-anythinggoes.xml}， 若失败则使用默认策略。同时预先清洗 "&nbsp;"
   * 和 "\"" 字符串，用于后续结果修正。
   */
  private XssUtils() {
    Policy policy = createPolicy();
    this.antiSamy = ObjectUtils.isNotEmpty(policy) ? new AntiSamy(policy) : new AntiSamy();
    this.nbsp = cleanHtml("&nbsp;");
    this.quot = cleanHtml("\"");
  }

  /**
   * 获取 XssUtils 的单例实例（线程安全、双重检查锁）。
   *
   * @return XssUtils 实例
   */
  private static XssUtils getInstance() {
    if (ObjectUtils.isEmpty(INSTANCE)) {
      synchronized (XssUtils.class) {
        if (ObjectUtils.isEmpty(INSTANCE)) {
          INSTANCE = new XssUtils();
        }
      }
    }

    return INSTANCE;
  }

  /**
   * 对外提供的 XSS 清洗入口。
   *
   * <p>执行步骤：
   *
   * <ol>
   *   <li>对输入字符串中的 HTML 实体（如 &amp;lt; ）进行反转义，因为 AntiSamy 扫描时会再次转义；
   *   <li>调用 AntiSamy 清洗；
   *   <li>将清洗结果中的乱码（&nbsp; 转换后的异常字符）替换为空字符串；
   *   <li>将双引号的乱码替换为原始双引号；
   *   <li>移除换行符。
   * </ol>
   *
   * @param taintedHTML 可能包含恶意脚本的原始字符串
   * @return 清洗后的安全字符串；若输入为 {@code null} 则返回 {@code null}
   */
  public static String cleaning(String taintedHTML) {
    // 对转义的HTML特殊字符（<、>、"等）进行反转义，因为AntiSamy调用scan方法时会将特殊字符转义
    String cleanHtml = StringEscapeUtils.unescapeHtml4(getInstance().cleanHtml(taintedHTML));
    // AntiSamy会把“&nbsp;”转换成乱码，把双引号转换成"&quot;" 先将&nbsp;的乱码替换为空，双引号的乱码替换为双引号
    String temp = cleanHtml.replaceAll(getInstance().nbsp, "");
    temp = temp.replaceAll(getInstance().quot, "\"");
    String result = temp.replaceAll("\n", "");
    if (log.isDebugEnabled()) {
      log.debug("Antisamy processing completed, {} -> {}", taintedHTML, result);
    }
    return result;
  }

  /**
   * 创建 AntiSamy 策略对象。
   *
   * <p>从 classpath 中的 {@code antisamy/antisamy-anythinggoes.xml} 加载策略文件。
   *
   * @return 策略对象；若加载失败则返回 {@code null}
   */
  private Policy createPolicy() {
    try {
      URL url = ResourceUtils.getURL("classpath:antisamy/antisamy-anythinggoes.xml");
      return Policy.getInstance(url);
    } catch (IOException | PolicyException e) {
      log.trace(
          "[COMMON-SPRING] An exception occurred during Antisamy strategy creation {}",
          e.getMessage());
      return null;
    }
  }

  /**
   * 调用 AntiSamy 进行扫描清洗。
   *
   * @param taintedHtml 待清洗的 HTML 字符串
   * @return AntiSamy 扫描结果
   * @throws ScanException 扫描过程中出现的异常
   * @throws PolicyException 策略验证异常
   */
  private CleanResults scan(String taintedHtml) throws ScanException, PolicyException {
    return antiSamy.scan(taintedHtml);
  }

  /**
   * 执行单次清洗并返回清洗后的 HTML。
   *
   * @param taintedHtml 待清洗的字符串
   * @return 清洗后的 HTML；如果扫描异常，则返回原始字符串
   */
  private String cleanHtml(String taintedHtml) {
    try {
      // 使用AntiSamy清洗数据
      final CleanResults cleanResults = scan(taintedHtml);
      return cleanResults.getCleanHTML();
    } catch (ScanException | PolicyException e) {
      log.trace("[COMMON-SPRING] An exception occurred during Antisamy scan {}", e.getMessage());
      return taintedHtml;
    }
  }
}
