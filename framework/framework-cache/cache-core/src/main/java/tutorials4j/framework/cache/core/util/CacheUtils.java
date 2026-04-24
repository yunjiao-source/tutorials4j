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
    public static final String PREFFIX_CACHE_NAME_DEFAULT = "tutorials4j";

    /**
     * 默认缓存前缀
     * @return
     */
    public static Supplier<String> defaultCacheNamePrefix() {
        return () -> PREFFIX_CACHE_NAME_DEFAULT + SymbolConsts.COLON
                + TenantContextHolder.get() + SymbolConsts.COLON;
    }



}
