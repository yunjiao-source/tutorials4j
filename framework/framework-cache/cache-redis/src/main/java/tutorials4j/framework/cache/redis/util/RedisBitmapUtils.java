package tutorials4j.framework.cache.redis.util;

import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.charset.StandardCharsets;

/**
 * Redis Bitmap 操作工具类。
 * <p>
 * 提供基于 Redis 的 bitmap 常用操作，包括设置/获取位值、统计位数为1的个数、以及位运算（AND/OR/XOR/NOT）。
 * 支持通过字符串参数自动计算哈希偏移量（使用 Murmur3_128 哈希算法，结果取绝对值），也支持直接指定偏移量。
 * </p>
 * <p>
 * <b>注意：</b>该类中的 {@link #stringRedisTemplate} 通过 Spring 的 {@literal @Autowired} 注入静态字段，
 * 使用时需确保 Spring 容器正确管理该 Bean，否则会引发空指针异常。
 * </p>
 *
 * @author Yun Jiao
 * @see StringRedisTemplate
 * @see RedisStringCommands.BitOperation
 */
public class RedisBitmapUtils {

    /**
     * Redis StringRedisTemplate 实例，用于执行 bitmap 操作。
     * 该字段由 Spring 自动注入，使用 {@code @Qualifier("stringRedisTemplate")} 指定注入的 Bean。
     */
    private static StringRedisTemplate stringRedisTemplate;

    /**
     * 对给定字符串进行 Murmur3_128 哈希计算，并返回其绝对值的整型值（作为 bitmap 偏移量）。
     *
     * @param key 待哈希的字符串（通常为业务唯一标识）
     * @return 非负整数偏移量，范围在 [0, Integer.MAX_VALUE] 之间
     */
    private static long hash(String key) {
        return Math.abs(Hashing.murmur3_128().hashObject(key, Funnels.stringFunnel(StandardCharsets.UTF_8)).asInt());
    }

    /**
     * 对指定的字符串参数进行哈希计算得到偏移量，然后设置 Redis bitmap 中该偏移量的位值。
     *
     * @param key   Redis 中存储 bitmap 的键名
     * @param param 需要哈希为偏移量的业务参数（如用户ID、标识符等）
     * @param value 要设置的位值，true 表示 1，false 表示 0
     * @return 该偏移量位置原来的位值（true 表示原为 1，false 表示原为 0）；若操作失败返回 null
     */
    public static Boolean setBit(String key, String param, boolean value) {
        return stringRedisTemplate.opsForValue().setBit(key, hash(param), value);
    }

    /**
     * 对指定的字符串参数进行哈希计算得到偏移量，然后获取 Redis bitmap 中该偏移量的位值。
     *
     * @param key   Redis 中存储 bitmap 的键名
     * @param param 需要哈希为偏移量的业务参数
     * @return 该偏移量的位值，true 表示 1，false 表示 0；如果键不存在，也返回 false
     */
    public static boolean getBit(String key, String param) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().getBit(key, hash(param)));
    }

    /**
     * 直接使用指定的偏移量设置 Redis bitmap 中该位置的位值。
     *
     * @param key    Redis 中存储 bitmap 的键名
     * @param offset 位偏移量（从 0 开始计数）
     * @param value  要设置的位值，true 表示 1，false 表示 0
     * @return 该偏移量位置原来的位值（true 表示原为 1，false 表示原为 0）；若操作失败返回 null
     */
    public static Boolean setBit(String key, long offset, boolean value) {
        return stringRedisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 直接使用指定的偏移量获取 Redis bitmap 中该位置的位值。
     *
     * @param key    Redis 中存储 bitmap 的键名
     * @param offset 位偏移量（从 0 开始计数）
     * @return 该偏移量的位值，true 表示 1，false 表示 0；如果键不存在，也返回 false
     */
    public static Boolean getBit(String key, long offset) {
        return stringRedisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 统计整个 bitmap 中值为 1 的位的总数。
     *
     * @param key Redis 中存储 bitmap 的键名
     * @return 位值为 1 的数量
     */
    public static Long bitCount(String key) {
        return stringRedisTemplate.execute((RedisCallback<Long>) connection -> connection.stringCommands().bitCount(key.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 统计 bitmap 中指定字节范围内的位值为 1 的数量。
     * <p>
     * 注意：Redis 的 BITCOUNT 命令的 start 和 end 参数是字节偏移量（非位偏移量），
     * 且支持负数索引（-1 表示最后一个字节）。
     * </p>
     *
     * @param key   Redis 中存储 bitmap 的键名
     * @param start 起始字节偏移量（包含）
     * @param end   结束字节偏移量（包含）
     * @return 指定字节范围内位值为 1 的数量
     */
    public static Long bitCount(String key, int start, int end) {
        return stringRedisTemplate.execute((RedisCallback<Long>) connection -> connection.stringCommands().bitCount(key.getBytes(), start, end));
    }

    /**
     * 对一个或多个 bitmap 执行位运算（AND、OR、XOR、NOT），并将结果保存到目标键中。
     * <p>
     * 注意：对于 NOT 操作，只能接受一个源键（destKey 长度应为 1）。
     * </p>
     *
     * @param op       位运算类型，支持 {@link RedisStringCommands.BitOperation#AND}、{@link RedisStringCommands.BitOperation#OR}、
     *                {@link RedisStringCommands.BitOperation#XOR}、{@link RedisStringCommands.BitOperation#NOT}
     * @param saveKey  存储运算结果的 Redis 键名
     * @param destKey  参与运算的一个或多个源键名
     * @return 结果 bitmap 的字节长度（即占用字节数），如果操作失败返回 null
     */
    public static Long bitOp(RedisStringCommands.BitOperation op, String saveKey, String... destKey) {
        byte[][] bytes = new byte[destKey.length][];
        for (int i = 0; i < destKey.length; i++) {
            bytes[i] = destKey[i].getBytes();
        }
        return stringRedisTemplate.execute((RedisCallback<Long>) connection -> connection.stringCommands().bitOp(op, saveKey.getBytes(), bytes));
    }

    /**
     * 执行位运算后，直接返回结果 bitmap 中位值为 1 的数量。
     * <p>
     * 该方法是对 {@link #bitOp(RedisStringCommands.BitOperation, String, String...)} 的便捷包装，
     * 依次执行位运算和 BITCOUNT，返回统计结果。
     * </p>
     *
     * @param op       位运算类型
     * @param saveKey  存储运算结果的 Redis 键名
     * @param destKey  参与运算的一个或多个源键名
     * @return 结果 bitmap 中值为 1 的位的个数
     */
    public static Long bitOpResult(RedisStringCommands.BitOperation op, String saveKey, String... destKey) {
        bitOp(op, saveKey, destKey);
        return bitCount(saveKey);
    }

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        RedisBitmapUtils.stringRedisTemplate = stringRedisTemplate;
    }
}