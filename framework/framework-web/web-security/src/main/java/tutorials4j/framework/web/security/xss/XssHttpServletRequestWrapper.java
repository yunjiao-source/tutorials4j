package tutorials4j.framework.web.security.xss;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.spring.util.XssUtils;

/**
 * 提供 XSS 防护能力的 HttpServletRequest 包装器。
 *
 * <p>重写 {@code getHeader}、{@code getParameter}、{@code getParameterValues} 和 {@code getParameterMap}
 * 方法， 对获取到的字符串或字符串数组使用 {@link XssUtils#cleaning(String)} 进行清洗，消除潜在的 XSS 攻击载荷。
 *
 * @author Yun Jiao
 * @see XssUtils
 * @see HttpServletRequestWrapper
 * @since 1.0
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
  /**
   * 构造 XSS 防护请求包装器。
   *
   * @param request 原始请求对象
   */
  public XssHttpServletRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  /**
   * 使用 AntiSamy 清洗单个字符串。
   *
   * @param value 需要清洗的原始字符串
   * @return 清洗后的安全字符串
   */
  private String cleaning(String value) {
    return XssUtils.cleaning(value);
  }

  /**
   * 使用 AntiSamy 清洗字符串数组中的每个元素。
   *
   * @param parameters 需要清洗的原始字符串数组
   * @return 清洗后的字符串数组（长度与输入相同）
   */
  private String[] cleaning(String[] parameters) {
    List<String> cleanParameters = Arrays.stream(parameters).map(XssUtils::cleaning).toList();
    String[] results = new String[cleanParameters.size()];
    return cleanParameters.toArray(results);
  }

  /** 获取请求头并对非空值执行 XSS 清洗。 */
  @Override
  public String getHeader(String name) {
    String header = super.getHeader(name);
    return StringUtils.isBlank(header) ? header : cleaning(header);
  }

  /** 获取请求参数并对非空值执行 XSS 清洗。 */
  @Override
  public String getParameter(String name) {
    String parameter = super.getParameter(name);
    return StringUtils.isBlank(parameter) ? parameter : cleaning(parameter);
  }

  /** 获取请求参数值数组并对非空数组执行 XSS 清洗。 */
  @Override
  public String[] getParameterValues(String name) {
    String[] parameterValues = super.getParameterValues(name);
    if (ArrayUtils.isNotEmpty(parameterValues)) {
      return cleaning(parameterValues);
    }
    return super.getParameterValues(name);
  }

  /** 获取请求参数映射并对所有参数值执行 XSS 清洗。 */
  @Override
  public Map<String, String[]> getParameterMap() {
    Map<String, String[]> parameterMap = super.getParameterMap();
    return parameterMap.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> cleaning(entry.getValue())));
  }
}
