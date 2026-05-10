package tutorials4j.framework.data.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import tutorials4j.framework.common.core.DefaultConsts;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存 {@link ConcurrentHashMap} 缓存数据源的路由管理器抽象实现。
 * <p>
 * 继承自 {@link AbstractDataSourceRoutingManager}，使用 {@code ConcurrentHashMap} 缓存已创建的数据源实例，
 * 当请求目标数据源时，若缓存中不存在则自动调用 {@link #createDataSource(String)} 创建并缓存。
 * </p>
 * <p>
 * 初始化时会将默认数据源以 {@link DefaultConsts#DEFAULT_TENTANT_CODE} 为键放入缓存。
 * </p>
 *
 * @author Yun Jiao
 * @see AbstractDataSourceRoutingManager
 * @see ConcurrentHashMap
 */
@Slf4j
public abstract class AbstractMapDataSourceRoutingManager extends AbstractDataSourceRoutingManager {
    protected final Map<String, DataSource> targetDataSources = new ConcurrentHashMap<>();

    /**
     * 使用默认数据源初始化路由管理器。
     * <p>
     * 调用父类 {@link #init(DataSource, Map)} 并传入空路由配置，
     * 同时将默认数据源存入缓存，键为 {@code DefaultConsts.DEFAULT_TENTANT_CODE}。
     * </p>
     *
     * @param dataSource 默认数据源
     */
    public void init(DataSource dataSource) {
        super.init(dataSource, Collections.emptyMap());
        targetDataSources.put(DefaultConsts.DEFAULT_TENTANT_CODE, dataSource);
    }

    /**
     * 根据名称确定要使用的目标数据源。
     * <p>
     * 首先校验名称非空，然后从缓存中获取或创建对应的数据源实例。
     * </p>
     *
     * @param name 路由名称，不能为 {@code null}
     * @return 对应的数据源
     * @throws IllegalArgumentException 如果名称为 {@code null}
     */
    @Override
    public DataSource determineTargetDataSource(String name) {
        Assert.notNull(name, "name must not be null");
        return targetDataSources.computeIfAbsent(name, this::createDataSource);
    }

    @Override
    public void shutdown() {
        Iterator<Map.Entry<String, DataSource>> it = targetDataSources.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, DataSource> entry = it.next();

            if (log.isDebugEnabled()) {
                log.debug("[DATA-JDBC] 关闭数据源：{}", entry.getKey());
            }
            try {
                doShutdown(entry.getValue());
            } catch (SQLException e) {
                log.error("关闭数据源异常", e);
            }

            it.remove();
        }

    }

    protected abstract void doShutdown(DataSource dataSource) throws SQLException;
}
