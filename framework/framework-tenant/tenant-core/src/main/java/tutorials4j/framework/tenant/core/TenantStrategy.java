package tutorials4j.framework.tenant.core;

/**
 * 租户策略
 *
 * @author Yun Jiao
 */
public enum TenantStrategy {
    /**
     * 路由数据源
     */
    ROUTING_DATABASE,
    /**
     * 基于Hibernate框架的独立数据库
     */
    HIBERNATE_DATABASE,

    /**
     * 基于Hibernate框架的共享表
     */
    HIBERNATE_TABLE
}
