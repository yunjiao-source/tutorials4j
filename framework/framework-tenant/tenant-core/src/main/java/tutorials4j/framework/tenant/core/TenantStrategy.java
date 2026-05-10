package tutorials4j.framework.tenant.core;

/**
 * 租户策略
 *
 * @author Yun Jiao
 */
public enum TenantStrategy {
    /**
     * 独立数据库
     */
    DATABASE,

    /**
     * 共享表
     */
    TABLE
}
