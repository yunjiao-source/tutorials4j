package tutorials4j.framework.cache.core;

import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.core.bean.TenantContextHolder;
import tutorials4j.framework.common.lang.SymbolConsts;

/**
 * 缓存工具
 *
 * @author Yun Jiao
 */
public  class CacheUtils {
    public static final String PREFFIX_CACHE_NAME_DEFAULT = "tutorials4j";

    /**
     * 默认缓存前缀
     * @return
     */
    public static String cacheNamePrefix() {
        return PREFFIX_CACHE_NAME_DEFAULT + SymbolConsts.COLON
                + TenantContextHolder.get() + SymbolConsts.COLON;
    }

    /**
     * 使用默认前缀组合
     * @param key
     * @return
     */
    public static String cacheName(String key) {
        String newKey = cacheNamePrefix() + key;
        if (!StringUtils.endsWith(key, SymbolConsts.COLON)) {
            newKey = newKey + SymbolConsts.COLON;
        }

        return newKey;
    }
}
