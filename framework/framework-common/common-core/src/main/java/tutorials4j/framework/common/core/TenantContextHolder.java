package tutorials4j.framework.common.core;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.util.concurrent.Striped;
import java.util.concurrent.locks.Lock;
import org.apache.commons.lang3.StringUtils;

/**
 * 存储/获取当前线程的租户信息
 *
 * @author Yun Jiao
 */
public class TenantContextHolder {
  private final Striped<Lock> striped = Striped.lock(256);
  private static final ThreadLocal<String> CURRENT_CONTEXT = new TransmittableThreadLocal<>();

  /**
   * 获取租户代码。注意：代码都会转换为大写
   *
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
