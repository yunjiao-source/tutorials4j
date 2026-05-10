package tutorials4j.framework.tenant.mybatis;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import tutorials4j.framework.common.core.TenantContextHolder;

import java.util.Set;

/**
 * 默认租户行处理器实现。
 * <p>
 * 从 {@link TenantContextHolder} 中获取当前线程的租户 ID，并将其作为 SQL 中租户列的值。
 * 支持忽略指定表，不对这些表进行租户 SQL 注入。
 * </p>
 *
 * @author Yun Jiao
 * @see TenantLineHandler
 */
@RequiredArgsConstructor
public class DefaultTenantLineHandler implements TenantLineHandler {
    private final Set<String> ignoreTables;

    @Override
    public Expression getTenantId() {
        return new StringValue(TenantContextHolder.get());
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return ignoreTables.contains(tableName);
    }
}
