package tutorials4j.framework.cache.core.template;

import cn.hutool.crypto.SecureUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;
import tutorials4j.framework.common.core.exception.CounterOverflowException;

/**
 * 基于 Redis 的计数器缓存模板抽象类。
 * <p>
 * 用于记录某个键（通常为业务标识）的访问次数，并提供次数上限控制。
 * 缓存值为 {@link Long} 类型，表示当前已计数的次数。
 * 核心方法 {@link #counting(String)} 和 {@link #counting(String, int, boolean)} 在每次调用时自增计数，
 * 当计数达到或超过最大允许次数时抛出 {@link CounterOverflowException}。
 * </p>
 * <p>
 * 默认采用普通字符串作为缓存键，可选择使用 MD5 对原始键进行摘要后再作为缓存键。
 * 首次计数时会通过 {@link #valueGenerator(String)} 初始化缓存值为 1L。
 * </p>
 *
 * @author Yun Jiao
 * @see AbstractRedisCacheTemplate
 * @see CounterOverflowException
 */
@Setter
@Getter
public abstract class AbstractCounterCacheTemplate extends AbstractRedisCacheTemplate<String, Integer> {
    private int maxTimes = 1;

    public AbstractCounterCacheTemplate(String cacheName) {
        super(cacheName);
    }

    @Override
    public Class<Integer> getValueClass() {
        return Integer.class;
    }

    @Override
    public Integer valueGenerator(String key) {
        return 1;
    }

    /**
     * 对指定键执行计数操作，使用构造时设置的 {@link #maxTimes} 作为上限。
     * <p>
     * 等价于调用 {@link #counting(String, int, boolean) counting(key, maxTimes, false)}。
     * </p>
     *
     * @param key 待计数的键
     * @return 当前计数后的值（已自增后的次数）
     * @throws CounterOverflowException 当计数达到或超过 {@code maxTimes} 时抛出
     */
    public int counting(String key) throws CounterOverflowException {
        return counting(key, maxTimes);
    }

    /**
     * 对指定键执行计数操作，使用指定的上限。
     * <p>
     * 等价于调用 {@link #counting(String, int, boolean) counting(key, maxTimes, false)}。
     * </p>
     *
     * @param key      待计数的键
     * @param maxTimes 最大允许计数次数
     * @return 当前计数后的值（已自增后的次数）
     * @throws CounterOverflowException 当计数达到或超过 {@code maxTimes} 时抛出
     */
    public int counting(String key, int maxTimes) throws CounterOverflowException {
        return counting(key, maxTimes, false);
    }

    public int counting(String key, boolean useMd5) throws CounterOverflowException {
        return counting(key, maxTimes, useMd5);
    }

    /**
     * 对指定键执行计数操作，可选择是否对键进行 MD5 摘要处理。
     * <p>
     * 计数逻辑：
     * <ol>
     *   <li>校验键不能为 {@code null}；</li>
     *   <li>若 {@code useMd5} 为 {@code true}，则对原始键进行 MD5 摘要作为实际缓存键；</li>
     *   <li>从缓存中获取当前计数值，若不存在则初始化为 0；</li>
     *   <li>若当前计数值为 0，则调用 {@link #create(Object)} 初始化缓存值为 1；</li>
     *   <li>否则将计数值加 1 后更新缓存，并判断加 1 前的值是否已达到 {@code maxTimes - 1}（即本次操作后会超过上限），若是则抛出异常；</li>
     *   <li>返回加 1 后的计数值。</li>
     * </ol>
     * </p>
     *
     * @param key      待计数的键
     * @param maxTimes 最大允许计数次数
     * @param useMd5   是否对键进行 MD5 摘要处理
     * @return 当前计数后的值（已自增后的次数）
     * @throws CounterOverflowException 当计数达到或超过 {@code maxTimes} 时抛出
     * @throws IllegalArgumentException 如果 {@code key} 为 {@code null}
     */
    public int counting(String key, int maxTimes, boolean useMd5) throws CounterOverflowException {
        Assert.notNull(key, "key cannot be null");

        String newKey = useMd5 ? SecureUtil.md5(key) : key;
        Integer index = get(newKey);
        if (index == null) {
            index = 0;
        }

        if (index == 0) {
            // 第一次读取剩余次数，因为缓存中还没有值，所以先创建缓存，同时缓存中计数为1。
            create(newKey);
        } else {
            if (index >= maxTimes) {
                throw new CounterOverflowException(maxTimes);
            }
            put(newKey, index + 1);
        }

        return index + 1;
    }
}
