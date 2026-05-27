package tutorials4j.springboot3.web.restversion.path;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

/**
 * 自定义条件类：从URL中提取版本号
 *
 * @author yangyunjiao
 */
@Getter
public class VersionPathRequestCondition implements RequestCondition<VersionPathRequestCondition> {
  private static final Pattern VERSION_PATTERN = Pattern.compile("/v(\\d+(\\.\\d+)?)");

  private final double apiVersion;

  public VersionPathRequestCondition(double apiVersion) {
    this.apiVersion = apiVersion;
  }

  /** 合并多个版本条件（比如类上和方法上都加了@ApiVersion） 优先级：方法上的版本 > 类上的版本 */
  @Override
  public VersionPathRequestCondition combine(VersionPathRequestCondition other) {
    return new VersionPathRequestCondition(other.apiVersion);
  }

  /** 匹配请求：从URL中提取版本号，判断是否匹配当前接口的版本 */
  @Override
  public VersionPathRequestCondition getMatchingCondition(HttpServletRequest request) {
    String originalUri = (String) request.getAttribute("originalUri");
    if (originalUri == null) {
      originalUri = request.getRequestURI();
    }

    Matcher matcher = VERSION_PATTERN.matcher(originalUri);
    if (matcher.find()) {
      String versionStr = matcher.group(1);
      try {
        double requestVersion = Double.parseDouble(versionStr);
        if (Math.abs(requestVersion - apiVersion) < 0.0001) { // 精确匹配
          return this;
        }
      } catch (NumberFormatException ignored) {
      }
    }

    // 如果没有版本号，默认匹配 1.0
    if (apiVersion == 1.0 && !originalUri.contains("/v")) {
      return this;
    }

    return null;
  }

  /** 多个接口匹配时，选择版本号最接近的（比如请求v1.1，优先匹配v1.1，其次v1.0） */
  @Override
  public int compareTo(VersionPathRequestCondition other, HttpServletRequest request) {
    return Double.compare(other.apiVersion, this.apiVersion); // 高版本优先
  }
}
