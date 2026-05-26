package tutorials4j.springboot3.data.orm.mybatistenant;

/**
 * 租户上下文
 *
 * @author Yun Jiao
 */
public class TenantContext {
  private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

  public static void setCurrentTenantId(String tenantId) {
    TENANT_ID.set(tenantId);
  }

  public static String getCurrentTenantId() {
    return TENANT_ID.get();
  }

  public static void clear() {
    TENANT_ID.remove();
  }
}
