package tutorials4j.springboot3;

/**
 * 数据源上下文持有者
 *
 * @author Yun Jiao
 */
public class DataSourceContextHolder {
  private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

  public static void setTenantId(String tenantId) {
    CONTEXT_HOLDER.set(tenantId);
  }

  public static String getTenantId() {
    return CONTEXT_HOLDER.get();
  }

  public static void clear() {
    CONTEXT_HOLDER.remove(); // 防止内存泄漏
  }
}
