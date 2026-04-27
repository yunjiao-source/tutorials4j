package tutorials4j.framework.cache.multi;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class MultiLevelCache implements Cache {
    private final Cache local;
    private final Cache remote;

    @Override
    public String getName() {
        return local.getName();
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(Object key) {
        // 1. 先查本地
        ValueWrapper wrapper = local.get(key);
        if (wrapper != null) {
            return wrapper;
        }
        // 2. 查远程
        wrapper = remote.get(key);
        if (wrapper != null) {
            // 3. 回填本地
            local.put(key, wrapper.get());
            return wrapper;
        }
        return null;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T value = local.get(key, type);
        if (value != null) {
            return value;
        }
        value = remote.get(key, type);
        if (value != null) {
            local.put(key, value);
        }
        return value;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T value = local.get(key, valueLoader);
        if (value != null) {
            return value;
        }
        value = remote.get(key, valueLoader);
        if (value != null) {
            local.put(key, value);
        }
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        local.put(key, value);
        remote.put(key, value);
    }

    @Override
    public void evict(Object key) {
        local.evict(key);
        remote.evict(key);
    }

    @Override
    public void clear() {
        local.clear();
        remote.clear();
    }
}
