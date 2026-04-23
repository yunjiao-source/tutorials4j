package tutorials4j.framework.common.core.bean;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.core.DefaultConsts;

/**
 * 存储/获取当前线程的租户信息
 *
 * @author Yun Jiao
 */
public class TenantContextHolder {
    private static final ThreadLocal<String> CURRENT_CONTEXT = new TransmittableThreadLocal<>();

    /**
     * 获取租户代码。注意：代码都会转换为大写
     * @return 租户代码（大写）
     */
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
