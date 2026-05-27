package tutorials4j.springboot3.web.restversion.header;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;
import lombok.Getter;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

/**
 * 自定义条件类：从Header提取版本号
 *
 * @author yangyunjiao
 */
@Getter
public class VersionHeaderRequestCondition
    implements RequestCondition<VersionHeaderRequestCondition> {
  // 版本号正则（匹配 /v1/、/v2.1/ 等格式）
  private static final Pattern VERSION_PATTERN = Pattern.compile("/v(\\d+(\\.\\d+)?)");

  private final double apiVersion;

  public VersionHeaderRequestCondition(double apiVersion) {
    this.apiVersion = apiVersion;
  }

  /** 合并多个版本条件（比如类上和方法上都加了@ApiVersion） 优先级：方法上的版本 > 类上的版本 */
  @Override
  public VersionHeaderRequestCondition combine(VersionHeaderRequestCondition other) {
    return new VersionHeaderRequestCondition(other.apiVersion);
  }

  /** 匹配请求：从URL中提取版本号，判断是否匹配当前接口的版本 */
  @Override
  public VersionHeaderRequestCondition getMatchingCondition(HttpServletRequest request) {
    // 从Header提取版本号
    String versionStr = request.getHeader("X-API-Version");
    // 或从参数提取：request.getParameter("version");
    if (versionStr != null && !versionStr.isEmpty()) {
      double requestVersion = Double.parseDouble(versionStr);
      if (requestVersion == this.apiVersion) { // 精确匹配
        return this;
      }
    }
    return this.apiVersion == 1.0 ? this : null;
  }

  /** 多个接口匹配时，选择版本号最接近的（比如请求v1.1，优先匹配v1.1，其次v1.0） */
  @Override
  public int compareTo(VersionHeaderRequestCondition other, HttpServletRequest request) {
    return Double.compare(other.apiVersion, this.apiVersion);
  }
}
