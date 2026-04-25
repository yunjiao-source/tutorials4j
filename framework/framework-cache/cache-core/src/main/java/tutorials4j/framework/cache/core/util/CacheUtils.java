package tutorials4j.framework.cache.core.util;

import tutorials4j.framework.common.core.SymbolConsts;
import tutorials4j.framework.common.core.bean.TenantContextHolder;

import java.util.function.Supplier;

/**
 * 缓存工具
 *
 * @author Yun Jiao
 */
public class CacheUtils {
    public static final String PREFIX_CACHE_NAME_DEFAULT = "tutorials4j";

    /**
     * 提供默认的缓存名称前缀生成器。
     * <p>
     * 生成的前缀格式为：{@code tutorials4j:{当前租户ID}:} 。
     * 其中租户ID通过 {@link TenantContextHolder#get()} 动态获取。
     * </p>
     *
     * @return 一个 {@link Supplier}，每次调用 {@link Supplier#get()} 都会根据当前租户上下文
     *         实时生成缓存前缀字符串。
     */
    public static Supplier<String> defaultCacheNamePrefix() {
        return () -> PREFIX_CACHE_NAME_DEFAULT + SymbolConsts.COLON
                + TenantContextHolder.get() + SymbolConsts.COLON;
    }



}
