package tutorials4j.framework.data.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import tutorials4j.framework.common.core.DefaultConsts;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public abstract class AbstractMapDataSourceRoutingManager extends AbstractDataSourceRoutingManager {
    protected final Map<String, DataSource> targetDataSources = new ConcurrentHashMap<>();

    public void init(DataSource dataSource) {
        super.init(dataSource, Collections.emptyMap());
        targetDataSources.put(DefaultConsts.DEFAULT_TENTANT_CODE, dataSource);
    }


    @Override
    public DataSource determineTargetDataSource(String name) {
        Assert.notNull(name, "name must not be null");
        return targetDataSources.computeIfAbsent(name, this::createDataSource);
    }

}
