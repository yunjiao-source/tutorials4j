package tutorials4j.framework.web.mvc.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.core.util.XssUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 提供 XSS 防护能力的 HttpServletRequest 包装器。
 * <p>
 * 重写 {@code getHeader}、{@code getParameter}、{@code getParameterValues} 和 {@code getParameterMap} 方法，
 * 对获取到的字符串或字符串数组使用 {@link XssUtils#cleaning(String)} 进行清洗，消除潜在的 XSS 攻击载荷。
 * </p>
 *
 * @author Yun Jiao
 * @see XssUtils
 * @see HttpServletRequestWrapper
 * @since 1.0
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
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


    @Override
    public String getHeader(String name) {
        String header = super.getHeader(name);
        return StringUtils.isBlank(header) ? header : cleaning(header);
    }

    @Override
    public String getParameter(String name) {
        String parameter = super.getParameter(name);
        return StringUtils.isBlank(parameter) ? parameter : cleaning(parameter);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] parameterValues = super.getParameterValues(name);
        if (ArrayUtils.isNotEmpty(parameterValues)) {
            return cleaning(parameterValues);
        }
        return super.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        return parameterMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> cleaning(entry.getValue())));
    }
}
