package tutorials4j.framework.examples.bitmap;

import com.google.common.hash.Hashing;
import tutorials4j.framework.cache.redis.util.RedisBitmapUtils;

/**
 * 布隆过滤器
 *
 * @author Yun Jiao
 */
public class BloomFilterHelper {
    private static final int BIT_SIZE = 1 << 24;  // 约 1600 万位，足够百万级数据
    private static final int[] SEEDS = {3, 5, 7, 11, 13}; // 多个哈希种子

    // 添加元素
    public static void add(String key, String element) {
        for (int seed : SEEDS) {
            long hash = hash(element, seed);
            RedisBitmapUtils.setBit(key, hash % BIT_SIZE, true);
        }
    }

    // 检查元素是否可能存在（false 表示一定不存在，true 表示可能存在）
    public static boolean mightContain(String key, String element) {
        for (int seed : SEEDS) {
            long hash = hash(element, seed);
            if (!RedisBitmapUtils.getBit(key, hash % BIT_SIZE)) {
                return false;
            }
        }
        return true;
    }

    private static long hash(String element, int seed) {
        // 可使用 Guava 或自行实现哈希
        return Math.abs(Hashing.murmur3_32(seed).hashUnencodedChars(element).asInt());
    }
}