package tutorials4j.framework.common.spring.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

/**
 * HTTP 请求包装器，用于在请求传递过程中模拟删除指定的请求头。
 *
 * @author Yun Jiao
 */
public class RemoveHeaderRequestWrapper extends HttpServletRequestWrapper {
  /** 需要从请求中移除的请求头名称集合。 */
  @Getter private final Set<String> headersToRemove;

  /**
   * 构造请求包装器，指定需要移除的请求头。
   *
   * @param request 原始 HTTP 请求对象
   * @param headersToRemove 需要移除的请求头名称
   */
  public RemoveHeaderRequestWrapper(HttpServletRequest request, String... headersToRemove) {
    super(request);
    this.headersToRemove = new HashSet<>(Arrays.asList(headersToRemove));
  }

  /**
   * 返回指定请求头的值，若该请求头在移除列表中则返回 null。
   *
   * @param name 请求头名称
   * @return 请求头值，被移除时返回 null
   */
  @Override
  public String getHeader(String name) {
    if (headersToRemove.contains(name)) {
      return null; // 模拟删除
    }
    return super.getHeader(name);
  }

  /**
   * 返回指定请求头的所有值，若该请求头在移除列表中则返回空枚举。
   *
   * @param name 请求头名称
   * @return 请求头值的枚举，被移除时返回空枚举
   */
  @Override
  public Enumeration<String> getHeaders(String name) {
    if (headersToRemove.contains(name)) {
      return Collections.emptyEnumeration();
    }
    return super.getHeaders(name);
  }

  /**
   * 返回过滤掉移除列表后的全部请求头名称。
   *
   * @return 剩余请求头名称的枚举
   */
  @Override
  public Enumeration<String> getHeaderNames() {
    // 收集原始 headerNames，并移除需要删除的 header
    Set<String> headerNames = new HashSet<>();
    Enumeration<String> original = super.getHeaderNames();
    while (original.hasMoreElements()) {
      String name = original.nextElement();
      if (!headersToRemove.contains(name)) {
        headerNames.add(name);
      }
    }
    return Collections.enumeration(headerNames);
  }
}
