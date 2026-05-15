package tutorials4j.framework.cache.core.lock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;
import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * 分布式锁服务工厂。
 *
 * <p>根据 {@link LockCacheType} 和 {@link LockType} 的组合，管理和提供对应的 {@link LockService} 实例。
 *
 * <p>通常与 Spring 配置结合，通过 {@link #setDistributedLockService(List)} 注入所有锁服务实现， 然后通过 {@link
 * #findLockService(Pair, Class)} 获取具体服务。
 *
 * @author Yun Jiao
 */
public class LockServiceFactory {
  private final Map<Pair<LockCacheType, LockType>, LockService> lockMap = new HashMap<>();

  /**
   * 根据锁模型和期望类型查找锁服务。
   *
   * @param model 锁模型（缓存类型 + 锁类型），不能为 {@code null}
   * @param type 期望的锁服务实现类，不能为 {@code null}
   * @param <T> 锁服务类型
   * @return 匹配的锁服务实例
   * @throws FrameworkRuntimeException 若未找到对应模型或类型不匹配
   */
  public <T extends LockService> T findLockService(
      Pair<LockCacheType, LockType> model, Class<T> type) {
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
        "分类【"
            + model
            + "】的锁实例类型为【"
            + lock.getClass().getName()
            + "】，与期望类型【"
            + type.getName()
            + "】不匹配");
  }

  /**
   * 注入所有锁服务实例。
   *
   * @param lockServices 锁服务列表，不能为 {@code null}，会按类型组合存入内部 Map
   */
  public void setDistributedLockService(List<LockService> lockServices) {
    for (LockService service : lockServices) {
      lockMap.put(Pair.of(service.getLockCacheType(), service.getLockType()), service);
    }
  }
}
