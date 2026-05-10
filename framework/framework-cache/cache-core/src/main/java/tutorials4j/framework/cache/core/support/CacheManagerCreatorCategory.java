package tutorials4j.framework.cache.core.support;

import lombok.Getter;
import tutorials4j.framework.common.core.support.BaseEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public enum CacheManagerCreatorCategory implements BaseEnum<String> {
    CAFFEINE(CacheManagerCreatorCategory.CAFFEINE_CREATOR, "本地缓存"),
    MULTI_LEVEL(CacheManagerCreatorCategory.MULTI_LEVEL_CREATOR, "两级缓存"),
    REDIS(CacheManagerCreatorCategory.REDIS_CREATOR, "REDIS缓存"),
    TENANT_CAFFEINE(CacheManagerCreatorCategory.TENANT_CAFFEINE_CREATOR, "本地缓存（租户）"),
    TENANT_MULTI_LEVEL(CacheManagerCreatorCategory.TENANT_MULTI_LEVEL_CREATOR, "两级缓存（租户）");

    public final static String CAFFEINE_CREATOR = "CAFFEINE_CREATOR";
    public final static String MULTI_LEVEL_CREATOR = "MULTI_LEVEL_CREATOR";
    public final static String REDIS_CREATOR = "REDIS_CREATOR";
    public final static String TENANT_CAFFEINE_CREATOR = "TENANT_CAFFEINE_CREATOR";
    public final static String TENANT_MULTI_LEVEL_CREATOR = "TENANT_MULTI_LEVEL_CREATOR";

    private final String code;
    @Getter
    private final String note;

    CacheManagerCreatorCategory(String code, String note) {
        this.code = code;
        this.note = note;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name();
    }

}
