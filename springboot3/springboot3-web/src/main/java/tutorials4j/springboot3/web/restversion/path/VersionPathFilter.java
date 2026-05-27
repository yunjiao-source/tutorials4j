package tutorials4j.springboot3.web.restversion.path;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 路径过滤器
 *
 * @author yangyunjiao
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class VersionPathFilter extends OncePerRequestFilter {

  private static final Pattern VERSION_PATTERN = Pattern.compile("^(/v\\d+(\\.\\d+)?)");

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String originalUri = request.getRequestURI();
    Matcher matcher = VERSION_PATTERN.matcher(originalUri);

    if (matcher.find()) {
      String versionPrefix = matcher.group(1);
      String newUri = originalUri.substring(versionPrefix.length());
      if (newUri.isEmpty()) {
        newUri = "/";
      }

      HttpServletRequestWrapper wrapper = new CustomHttpServletRequestWrapper(request, newUri);

      // 保存原始 URI 供 Condition 使用
      request.setAttribute("originalUri", originalUri);
      chain.doFilter(wrapper, response);
    } else {
      request.setAttribute("originalUri", originalUri);
      chain.doFilter(request, response);
    }
  }

  public static class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final String uri;

    public CustomHttpServletRequestWrapper(HttpServletRequest request, String newUri) {
      super(request);
      this.uri = newUri;
    }

    @Override
    public String getRequestURI() {
      return uri;
    }

    @Override
    public StringBuffer getRequestURL() {
      StringBuffer url = new StringBuffer();
      url.append(getScheme())
          .append("://")
          .append(getServerName())
          .append(":")
          .append(getServerPort())
          .append(getRequestURI());
      return url;
    }
  }
}
