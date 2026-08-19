package tutorials4j.framework.common.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * 异常工具
 *
 * @author Yun Jiao
 */
public class ExceptionUtils {

  /**
   * 获取只包含 tutorials4j 项目包内类名的异常堆栈字符串。
   *
   * @param throwable 需要获取堆栈的异常对象
   * @return 过滤后的堆栈字符串，异常为 null 时返回空字符串
   */
  public static String getSelfStackTrace(Throwable throwable) {
    return getStackTrace(throwable, "tutorials4j");
  }

  /**
   * 按允许的包名前缀过滤异常堆栈，递归处理整个异常链（含 Caused by）。
   *
   * @param throwable 需要获取堆栈的异常对象
   * @param allowedPackagePrefix 允许保留的包名前缀，可传多个
   * @return 过滤后的堆栈字符串，异常为 null 或前缀为空时返回空字符串
   */
  public static String getStackTrace(Throwable throwable, String... allowedPackagePrefix) {
    if (throwable == null || allowedPackagePrefix == null || allowedPackagePrefix.length == 0) {
      return "";
    }
    // 转换为前缀列表，方便匹配
    List<String> prefixes = Arrays.asList(allowedPackagePrefix);
    // 定义过滤条件：只要堆栈元素的类名以任一允许的前缀开头则保留
    java.util.function.Predicate<StackTraceElement> filter =
        element -> prefixes.stream().anyMatch(prefix -> element.getClassName().startsWith(prefix));

    // 递归过滤整个异常链
    return getFilteredStackTrace(throwable, filter);
  }

  /**
   * 递归过滤并格式化异常链的堆栈信息。
   *
   * @param throwable 需要格式化的异常对象
   * @param filter 堆栈元素过滤条件
   * @return 过滤后的堆栈字符串
   */
  private static String getFilteredStackTrace(
      Throwable throwable, Predicate<StackTraceElement> filter) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    pw.println(throwable.toString());

    for (StackTraceElement element : throwable.getStackTrace()) {
      if (filter.test(element)) {
        pw.println("\tat " + element);
      }
    }

    // 处理嵌套异常（cause）
    Throwable cause = throwable.getCause();
    if (cause != null) {
      pw.println("Caused by: ");
      pw.print(getFilteredStackTrace(cause, filter));
    }

    pw.close();
    return sw.toString();
  }
}
