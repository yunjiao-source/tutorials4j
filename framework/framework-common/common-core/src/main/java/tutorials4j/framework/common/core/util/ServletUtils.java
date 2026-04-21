package tutorials4j.framework.common.core.util;

import jakarta.servlet.http.HttpServletRequest;
import tutorials4j.framework.common.lang.DefaultConsts;

/**
 * http 头工具
 *
 * @author Yun Jiao
 */
public class ServletUtils{
    public static String getTenant(HttpServletRequest httpServletRequest) {
        return getHeader(httpServletRequest, DefaultConsts.HTTP_HEADER_TENANT);
    }

    public static String getHeader(HttpServletRequest httpServletRequest, String name) {
        return httpServletRequest.getHeader(name);
    }
}
