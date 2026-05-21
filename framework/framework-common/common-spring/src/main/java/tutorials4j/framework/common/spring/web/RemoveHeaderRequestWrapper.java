package tutorials4j.framework.common.spring.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class RemoveHeaderRequestWrapper extends HttpServletRequestWrapper {

  private final Set<String> headersToRemove;

  public RemoveHeaderRequestWrapper(HttpServletRequest request, String... headersToRemove) {
    super(request);
    this.headersToRemove = new HashSet<>(Arrays.asList(headersToRemove));
  }

  @Override
  public String getHeader(String name) {
    if (headersToRemove.contains(name)) {
      return null; // 模拟删除
    }
    return super.getHeader(name);
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    if (headersToRemove.contains(name)) {
      return Collections.emptyEnumeration();
    }
    return super.getHeaders(name);
  }

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
