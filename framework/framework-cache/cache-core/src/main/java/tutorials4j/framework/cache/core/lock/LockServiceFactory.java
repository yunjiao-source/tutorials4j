package tutorials4j.framework.cache.core.lock;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;
import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class LockServiceFactory {
    private final Map<Pair<LockCacheType, LockType>, LockService> lockMap = new HashMap<>();

    public <T extends LockService> T findLockService(Pair<LockCacheType, LockType> model, Class<T> type) {
        Assert.notNull(model, "model must not be null");
        Assert.notNull(type, "type must not be null");

        LockService lock = lockMap.get(model);
        if (lock == null) {
            throw new FrameworkRuntimeException("未找到分类为【" + model + "】的分布式锁");
        }

        if (type.isInstance(lock)) {
            return type.cast(lock);
        }

        // 4. 类型不匹配异常
        throw new FrameworkRuntimeException(
                "分类【" + model + "】的锁实例类型为【" + lock.getClass().getName() +
                        "】，与期望类型【" + type.getName() + "】不匹配"
        );
    }

    public void setDistributedLockService(List<LockService> lockServices) {
        for (LockService service : lockServices) {
            lockMap.put(Pair.of(service.getLockCacheType(), service.getLockType()), service);
        }

    }
}
