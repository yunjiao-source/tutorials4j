package tutorials4j.framework.cache.core.support;

import lombok.Getter;
import tutorials4j.framework.common.core.support.BaseEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public enum CacheManagerCreatorCategory implements BaseEnum<Integer> {
    CAFFEINE(1, "本地缓存"),
    MULTI_LEVEL(2, "两级缓存"),
    REDIS(3, "REDIS缓存"),
    TENANT_CAFFEINE(4, "本地缓存（租户）"),
    TENANT_MULTI_LEVEL(5, "两级缓存（租户）");


    private final Integer code;
    @Getter
    private final String note;

    CacheManagerCreatorCategory(Integer code, String note) {
        this.code = code;
        this.note = note;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name();
    }

}
