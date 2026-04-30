package tutorials4j.framework.common.core.support;

import tutorials4j.framework.common.core.JdbcOptions;

import javax.sql.DataSource;

/**
 * 数据源路由管理器接口。
 * <p>
 * 定义多租户环境下根据租户标识动态选择目标数据源的能力，
 * 并提供添加路由 JDBC 配置及获取默认数据源的方法。
 * </p>
 *
 * @author Yun Jiao
 * @see DataSource
 * @see JdbcOptions
 */
public interface DataSourceRoutingManager {
    /**
     * 根据名称确定要使用的目标数据源。
     *
     * @param name 名称唯一标识（例如租户ID）
     * @return 对应名称的数据源
     */
    DataSource determineTargetDataSource(String name);

    /**
     * 获取默认数据源。
     *
     * @return 默认数据源（主数据源）
     */
    DataSource getDefaultDataSource();

    /**
     * 添加一条路由 JDBC 配置。
     * <p>
     * 将指定名称的 JDBC 连接选项注册到路由管理器中，以便后续动态创建数据源时使用。
     * </p>
     *
     * @param name         路由名称（租户标识）
     * @param jdbcOptions  JDBC 连接选项，包含驱动、URL、用户名、密码等
     */
    void addRoutingJdbcOptions(String name, JdbcOptions jdbcOptions);
}
