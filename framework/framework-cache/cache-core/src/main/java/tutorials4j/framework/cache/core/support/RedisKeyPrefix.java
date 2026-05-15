package tutorials4j.framework.cache.core.support;

import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.core.SymbolConsts;
import tutorials4j.framework.common.core.TenantContextHolder;

/**
 * Redis 缓存 Key 前缀生成策略的函数式接口。
 * <p>
 * 用于为不同的缓存名称（cacheName）自动添加特定前缀，如租户隔离标识或自定义前缀。
 * 同时提供了静态工具方法用于解析已添加前缀的 Key，以及常用的工厂方法生成特定策略实例。
 * </p>
 *
 * @author Yun Jiao
 * @see #tenant()
 * @see #tenantPrefixed(String)
 * @see #uncompute(String)
 */
@FunctionalInterface
public interface RedisKeyPrefix {
    /**
     * 默认的分隔符，用于连接前缀与原始 Key 名称。
     * <p>值为 {@value}，取自 {@link SymbolConsts#COLON}。</p>
     */
    String SEPARATOR = SymbolConsts.COLON;

    /**
     * 根据给定的缓存名称计算完整 Redis Key。
     *
     * @param cacheName 原始缓存名称（通常为业务标识或缓存区域名）
     * @return 添加了前缀后的完整 Key，用于 Redis 存储
     */
    String compute(String cacheName);

    /**
     * 从完整的 Redis Key 中解析出原始缓存名称（移除最前一段前缀）。
     * <p>
     * 规则：若 Key 为空或空白，则原样返回；否则查找第一个冒号（{@value #SEPARATOR}），
     * 若找不到冒号则返回原 Key，否则返回冒号之后的所有字符。
     * </p>
     * <p>
     * 示例：
     * <pre>
     * RedisKeyPrefix.uncompute("tenant1:user:123")  → "user:123"
     * RedisKeyPrefix.uncompute("no-colon-key")      → "no-colon-key"
     * RedisKeyPrefix.uncompute(null)                → null
     * </pre>
     *
     * @param key 完整的 Redis Key（可能包含前缀）
     * @return 移除第一个前缀部分后的原始 Key
     */
    static String uncompute(String key) {
        if (StringUtils.isBlank(key)) {
            return key;
        }
        int firstColonIndex = key.indexOf(SEPARATOR);
        if (firstColonIndex == -1) {
            return key;
        }
        return key.substring(firstColonIndex + 1);
    }

    /**
     * 创建一个使用当前租户 ID 作为前缀的 {@link RedisKeyPrefix} 实例。
     * <p>
     * 生成的 Key 格式为：<code>{租户ID}:{cacheName}</code>。
     * 租户 ID 通过 {@link TenantContextHolder#get()} 动态获取。
     * </p>
     * <p>
     * 典型用途：多租户系统中为不同租户的数据实现自然隔离。
     * </p>
     *
     * @return 基于租户的前缀策略函数
     * @see TenantContextHolder
     */
    static RedisKeyPrefix tenant() {
        return name -> TenantContextHolder.get() + SEPARATOR + name;
    }

    /**
     * 创建一个结合租户 ID 和自定义固定前缀的 {@link RedisKeyPrefix} 实例。
     * <p>
     * 生成的 Key 格式取决于传入的 {@code prefix} 是否以分隔符结尾：
     * <ul>
     *     <li>若 {@code prefix.endsWith(SEPARATOR)}，格式为：<code>{租户ID}:{prefix}{cacheName}</code></li>
     *     <li>否则格式为：<code>{租户ID}:{prefix}{SEPARATOR}{cacheName}</code></li>
     * </ul>
     * </p>
     * <p>
     * 示例：
     * <pre>
     * // prefix = "order"
     * tenantPrefix("order") → "tenant123:order:cacheName"
     *
     * // prefix = "order:"
     * tenantPrefix("order:") → "tenant123:order:cacheName"
     * </pre>
     * </p>
     *
     * @param prefix 自定义前缀字符串，不能为 {@code null}
     * @return 结合租户和自定义前缀的策略函数
     * @throws IllegalArgumentException 如果 prefix 为 {@code null}
     */
    static RedisKeyPrefix tenantPrefixed(String prefix) {
        if (StringUtils.isBlank(prefix)) {
            return tenant();
        }

        return name -> TenantContextHolder.get() + SEPARATOR + prefix + name;
    }


}
