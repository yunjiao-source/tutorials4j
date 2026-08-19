package tutorials4j.framework.tenant.core;

/**
 * 租户隔离策略枚举：DATABASE 表示每个租户使用独立数据库，TABLE 表示所有租户共享表结构并通过租户字段隔离数据。
 *
 * @author Yun Jiao
 */
public enum TenantStrategy {
  /** 独立数据库 */
  DATABASE,

  /** 共享表 */
  TABLE
}
