package tutorials4j.framework.common.core.support;

import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class HandlerInterceptorOptions {
    private String[] includePathPatterns = new String[]{"/**"};
    private String[] excludePathPatterns = new String[]{};
}
