package tutorials4j.framework.data.core.tenant;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.lang.DefaultConsts;

/**
 * 存储/获取当前线程的租户信息
 *
 * @author Yun Jiao
 */
public class TenantContextHolder {
    private static final ThreadLocal<String> CURRENT_CONTEXT = new TransmittableThreadLocal<>();

    public static String get() {
        String tenant = CURRENT_CONTEXT.get();
        if (StringUtils.isBlank(tenant)) {
            tenant = DefaultConsts.DEFAULT_TENTANT_CODE;
        }
        return tenant.toUpperCase();
    }

    public static void set(final String tenant) {
        CURRENT_CONTEXT.set(tenant);
    }

    public static void clear() {
        CURRENT_CONTEXT.remove();
    }
}
