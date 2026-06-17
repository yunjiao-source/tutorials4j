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
public class ExceptionUtils extends org.apache.commons.lang3.exception.ExceptionUtils {
  public static String getSelfStackTrace(Throwable throwable) {
    return getStackTrace(throwable, "tutorials4j");
  }

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
